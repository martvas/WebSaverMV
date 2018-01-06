package model.network;

import library.ServiceMessage;
import model.file.WebSaverFile;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;

public class ServerServiceThread extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private boolean connectedToServer;
    private ServerServiceListener serverServiceListener;

    private String nextFileName;
    private String derictoryToSaveFile;

    private String nextFileNameGetFromServer;
    private String nextFileMd5Hash;

    private WebSaverFile wsFile;

    public ServerServiceThread(ServerServiceListener serverServiceListener) {
        wsFile = new WebSaverFile();
        this.serverServiceListener = serverServiceListener;
        this.connectedToServer = false;
    }

    private void tryToConnect() {
        while (!connectedToServer) {
            try {
                sendResponseToView(ServiceMessage.TRY_TO_CONNECT);
                socket = new Socket("localhost", 8189);
                connectedToServer = true;
            } catch (ConnectException e) {
                sendResponseToView(ServiceMessage.NO_CONNECT);

                //Засыпаю и попробую снова через 2 секунды
                try {
                    sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                closeSocketsAndStreams();
            }

        }
    }

    @Override
    public void run() {
        try {
            tryToConnect();
            sendResponseToView(ServiceMessage.CONNECTED_TO_SERVER);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.flush();

            while (true) {
                Object response = new Object();
                while (true) {
                    response = in.readObject();
                    if (response instanceof String) {
                        System.out.println("Server response: " + response.toString());
                        responseStringHandler(response.toString());
                    }
                    if (response instanceof byte[]) {
                        responseByteArrHandler((byte[]) response);
                    }
                }
            }
        } catch (SocketException e) {
            sendResponseToView(ServiceMessage.CONNECTION_LOST);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSocketsAndStreams();
        }
    }

    //Метод, который сохраняет массив байтов в файл
    private synchronized void responseByteArrHandler(byte[] response) {
        //Проверяем одинаковое ли отосланное название файла и которое будет принято + существует ли директория
        if (derictoryToSaveFile != null && nextFileName.equals(nextFileNameGetFromServer)) {
            if (wsFile.saveFileOnComputer(derictoryToSaveFile, nextFileName, response, nextFileMd5Hash)) {
                sendResponseToView(ServiceMessage.FILE_SAVED + ":" + "Файл успешно сохранился в " + derictoryToSaveFile + "\\" + nextFileName);
            } else sendResponseToView(ServiceMessage.MAIN_INFO + ":" + nextFileName + "Не сохранился");

            //Обнуляю переменные для название и имя файла,
            nextFileName = null;
            nextFileMd5Hash = null;
            nextFileMd5Hash = null;
        }
    }

    private synchronized void sendRequest(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Послать массив байтов
    private synchronized void sendFileInBytes(byte[] fileContent) {
        try {
            out.writeObject(fileContent);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private synchronized void responseStringHandler(String response) {

        String[] responseArr = response.split(":");
        String responseID = responseArr[0];

        if (responseID.equals(ServiceMessage.SAVE_FILE_INFO)) {
            infoAboutNextFileFromServer(responseArr[1], responseArr[2]);
        } else sendResponseToView(response);

    }

    private synchronized void infoAboutNextFileFromServer(String filename, String hashcode) {
        this.nextFileNameGetFromServer = filename;
        this.nextFileMd5Hash = hashcode;
    }

    public synchronized void loginRequest(String username, String password) {
        String loginRequest = ServiceMessage.LOGIN_ATTEMPT + ":" + username + ":" + password;
        sendRequest(loginRequest);
    }

    public synchronized void registrationRequest(String username, String password) {
        String regRequest = ServiceMessage.REGISTR + ":" + username + ":" + password;
        sendRequest(regRequest);
    }

    public synchronized void deleteFileRequest(String fileName) {
        sendRequest(ServiceMessage.DELETE_FILE + ":" + fileName);
    }

    public synchronized void saveFileRequest(String fileName, String derictoryToSaveFile) {
        sendRequest(ServiceMessage.SAVE_FILE + ":" + fileName);

        //записываю в переменные название, чтобы потом сравнить при получении
        this.nextFileName = fileName;
        this.derictoryToSaveFile = derictoryToSaveFile;
    }

    public synchronized void addFileToServer(File file) {
        String fileName = file.getName();
        String fileHash = wsFile.getFileMd5Hash(file.toPath());
        addFileRequest(fileName, fileHash);

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            sendFileInBytes(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void addFileRequest(String filename, String fileHash) {
        String addFileString = ServiceMessage.ADD_FILE + ":" + filename + ":" + fileHash;
        sendRequest(addFileString);
    }

    private void sendResponseToView(String msg) {
        serverServiceListener.serverResponse(this, msg);
    }

    private void closeSocketsAndStreams() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
