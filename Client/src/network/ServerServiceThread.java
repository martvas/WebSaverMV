package network;

import gui.ClientGui;
import gui.LoginMenuGui;
import gui.MainMenuGui;
import gui.RegMenuGui;
import library.ServiceMessage;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ServerServiceThread extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private boolean connectedToServer;
    private ClientGui clientGui;

    private LoginMenuGui loginMenuGui;
    private RegMenuGui regMenuGui;
    private MainMenuGui mainMenuGui;

    private String nextFileName;
    private String nextFileSize;

    public ServerServiceThread(ClientGui clientGui) {
        this.clientGui = clientGui;
        this.connectedToServer = false;
    }

    String[][] files2dArr;

    public void tryToConnect() {
        while (!connectedToServer) {
            try {
                clientGui.getLoginMenuGUI().setInfo("Пытаюсь присоединиться к серверу");
                socket = new Socket("localhost", 8189);
                connectedToServer = true;
            } catch (ConnectException e) {
                clientGui.getLoginMenuGUI().setInfo("Не смог подключиться к серверу");

                //Засыпаю и попробую снова через 2 секунды
                try {
                    sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            loginMenuGui = clientGui.getLoginMenuGUI();
            regMenuGui = clientGui.getRegMenuGui();

            tryToConnect();
            loginMenuGui.setInfo("Подключился к серверу");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.flush();

            while (true) {
                Object response = new Object();
                while (true) {
                    response = in.readObject();
                    if (response instanceof String) {
                        System.out.println("Server response: " + response.toString());
                        responseHandler(response.toString());
                    }

                    if (response instanceof byte[]) {
                        responseByteArrHandler((byte[]) response);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Метод, который сохраняет массив байтов в файл
    private void responseByteArrHandler(byte[] response) {
        String directory = mainMenuGui.getDirectoryToSaveFile();
        byte[] fileByteArr = response;

        //Проверяем одинаковый ли размер у массива байта и отправленного запроса до этого + существует ли директория
        if (directory != null && Integer.parseInt(nextFileSize) == fileByteArr.length) {

            if (saveFileOnComputer(directory, nextFileName, fileByteArr)) {
                mainMenuGui.setInfo("Файл успешно сохранился в " + directory + nextFileName);
            } else mainMenuGui.setInfo(nextFileName + "Не сохранился");

            //Обнуляю переменные для название и имя файла,
            nextFileName = null;
            nextFileSize = null;
        }
    }

    public synchronized void sendRequest(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Послать массив байтов
    public synchronized void sendFileInBytes(byte[] fileContent) {
        try {
            out.writeObject(fileContent);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void responseHandler(String response) {
        String[] responseArr = response.split(":");
        String responseID = responseArr[0];
        switch (responseID) {
            case (ServiceMessage.LOGIN_ACCEPTED):
                loginAccepted(responseArr);
                break;
            case (ServiceMessage.UPDATE):
                updateTable(responseArr);
                break;
            case (ServiceMessage.LOGIN_CANCELED):
                loginMenuGui.setInfo("Имя или пароль введены не правильно");
                break;
            case (ServiceMessage.REGISTRATION_ACCEPTED):
                loginMenuGui.setVisible(true);
                clientGui.setVisible(false);
                loginMenuGui.setInfo("Регистрация прошла успешна. Введите данные");
                break;
            case (ServiceMessage.REGISTRATION_CANCELED):
                regMenuGui.setInfoReg("Данный аккаунт уже зарегистрирован");
                break;
            default:
                System.out.println("Ответ сервера не известен" + response);
                break;
        }
    }

    //Метод, который получает строку из сервера, преобразует в двумерный массив и обновляет таблицу после изменения
    public void updateTable(String[] responseArr) {
        if (responseArr.length > 0) {
            String[] arrTable = Arrays.copyOfRange(responseArr, 1, responseArr.length);
            files2dArr = getFiles2dArr(arrTable);
        } else System.out.println("Net dannih iz bazy");
        mainMenuGui.updateTable(files2dArr);
        return;
    }


    //Если произошел успешный логин
    public void loginAccepted(String[] responseArr) {
        loginMenuGui.setVisible(false);
        if (responseArr.length > 0) {
            String[] filesArr = Arrays.copyOfRange(responseArr, 1, responseArr.length);
            files2dArr = getFiles2dArr(filesArr);
        } else System.out.println("");
        clientGui.initMainMenu(files2dArr);
        mainMenuGui = clientGui.getMainMenuGui();
        return;
    }

    //Преобразует одномерный массив в двумерный (для таблицы)
    public String[][] getFiles2dArr(String[] stringArr) {
        int numCell = 0;
        int rows = stringArr.length / 2;
        String[][] string2dArr = new String[rows][2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < 2; j++) {
                string2dArr[i][j] = stringArr[numCell++];
            }
        }
        return string2dArr;
    }

    public void loginRequest(String username, String password) {
        String loginRequest = ServiceMessage.LOGIN_ATTEMPT + ":" + username + ":" + password;
        sendRequest(loginRequest);
    }

    public void registrationRequest(String username, String password) {
        String regRequest = ServiceMessage.REGISTR + ":" + username + ":" + password;
        sendRequest(regRequest);
    }

    public void deleteFileRequest(String fileName, String fileSize) {
        sendRequest(ServiceMessage.DELETE_FILE + ":" + fileName + ":" + fileSize);
    }


    public void saveFileRequest(String fileName, String fileSize) {
        sendRequest(ServiceMessage.SAVE_FILE + ":" + fileName + ":" + fileSize);
        //записываю в переменные имя и название, чтобы потом сравнить при аолучении массива байтов из файла
        nextFileName = fileName;
        nextFileSize = fileSize;
    }

    public void addFileRequest(String filename, String filesize) {
        String addFileString = ServiceMessage.ADD_FILE + ":" + filename + ":" + filesize;
        sendRequest(addFileString);
    }

    public boolean saveFileOnComputer(String directory, String fileName, byte[] fileByteArr) {
        Path targetPath = Paths.get(directory + "/" + fileName);

        //Проверка есть ли файл с таким же названием
        int n = 1;
        String fileNameWoutExt = FilenameUtils.removeExtension(fileName);
        String fileExtension = FilenameUtils.getExtension(fileName);
        while (Files.exists(targetPath)) {
            targetPath = Paths.get(directory + "/" + fileNameWoutExt + "(" + n + ")." + fileExtension);
            n++;
        }

        try {
            Files.write(targetPath, fileByteArr);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
