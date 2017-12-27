package network;

import database.DataBase;
import fileWork.FileWork;
import library.ServiceMessage;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;


/*
Что сделать (для меня заметка)
   1) 56 строка    // !!! - Переписать ответ если нет

 */
public class ClientServiceThread implements Runnable {
    private final Socket socket;
    private String clientUserName;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private DataBase dataBase;
    private FileWork fileWork;

    private String nextFileName;
    private String nextFileSize;


    public ClientServiceThread(Socket socket, DataBase dataBase, FileWork fileWork) {
        this.socket = socket;
        this.dataBase = dataBase;
        this.fileWork = fileWork;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.flush();

            while (true) {
                Object request = new Object();
                while (true) {
                    request = in.readObject();
                    if (request instanceof String) {
                        requestHandler(request.toString());
                    }
                    if (request instanceof byte[]) {
                        byte[] fileByteArr = (byte[]) request;

                        //Проверяем одинаковый ли размер у массива байта и запроса, который пришел до этого из строки
                        if (Integer.parseInt(nextFileSize) == fileByteArr.length) {

                            // !!! - Переписать ответ если нет
                            if (fileWork.saveFileOnServer(clientUserName, nextFileName, fileByteArr)) {
                                System.out.println("Файл учпешно сохранился " + nextFileName);
                                sendResponse(ServiceMessage.UPDATE + ":" + fileWork.getFileCatalog(clientUserName));
                            } else System.out.println(nextFileName + "Не сохранился");

                            //Обнуляю название и имя файла
                            nextFileName = null;
                            nextFileSize = null;

                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Клиент отключился");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void requestHandler(String request) {
        System.out.println(request);
        String[] requestArr = request.split(":");
        String requestID = requestArr[0];

        switch (requestID) {
            case ServiceMessage.LOGIN_ATTEMPT:
                login(requestArr[1], requestArr[2]);
                break;
            case ServiceMessage.REGISTR:
                registration(requestArr[1], requestArr[2]);
                break;
            case ServiceMessage.ADD_FILE:
                addFileToServer(requestArr[1], requestArr[2]);
                break;
            case ServiceMessage.DELETE_FILE:
                deleteFile(requestArr[1]);
                break;
            case ServiceMessage.SAVE_FILE:
                sendFileToClient(requestArr[1]);
                break;
            default:
                System.out.println("Неизвестный тип запроса от клиента");
                break;
        }
    }

    private void addFileToServer(String fileName, String fileSize) {
        this.nextFileName = fileName;
        this.nextFileSize = fileSize;
    }

    public synchronized void sendResponse(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void login(String username, String password) {
        if (dataBase.loginRequest(username, password)) {
            //Даю имя для клиентЮзернейма, которая в дальнейшем используется как ИД
            clientUserName = username;
            String table = fileWork.getFileCatalog(clientUserName);
            sendResponse(ServiceMessage.LOGIN_ACCEPTED + ":" + table);
        } else sendResponse(ServiceMessage.LOGIN_CANCELED);
    }

    public void registration(String userName, String password) {
        if (dataBase.registration(userName, password) && fileWork.makeDir(userName)) {
            sendResponse(ServiceMessage.REGISTRATION_ACCEPTED);
        } else sendResponse(ServiceMessage.REGISTRATION_CANCELED);
    }

    //---!! Переписать ответ
    //Если да то упдэйт, нет то послать ответ что не получилось
    public void deleteFile(String filename) {
        if (fileWork.deleteFile(clientUserName, filename)) {
            sendResponse(ServiceMessage.UPDATE + ":" + fileWork.getFileCatalog(clientUserName));
        }
        sendResponse(ServiceMessage.UPDATE + ":" + fileWork.getFileCatalog(clientUserName));
    }

    //Возможно переписать и в другой класс добавить мб
    public void sendFileToClient(String fileName) {
        File file = new File("Server/clients_folder/" + clientUserName + "/" + fileName);

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            sendFileInBytes(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void sendFileInBytes(byte[] fileContent) {
        try {
            out.writeObject(fileContent);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
