package network;

import database.DataBase;
import file.ClientFile;
import library.ServiceMessage;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;

public class ClientServiceThread implements Runnable {
    private final Socket socket;
    private String clientUserName;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private DataBase dataBase;
    private ClientFile clientFile;

    private String nextFileName;
    private String nextFileHash;


    public ClientServiceThread(Socket socket, DataBase dataBase, ClientFile clientFile) {
        this.socket = socket;
        this.dataBase = dataBase;
        this.clientFile = clientFile;
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
                        tryToSaveFile((byte[]) request);
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Клиент отключился");
            clientUserName = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSocketsAndStreams();
        }
    }

    private synchronized void requestHandler(String request) {
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
                addFileToServerRequest(requestArr[1], requestArr[2]);
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

    private synchronized void tryToSaveFile(byte[] request) {
        //Проверяем одинаковый ли размер у массива байта и запроса, который пришел до этого из строки

            if (clientFile.saveFileOnServer(clientUserName, nextFileName, request, nextFileHash)) {
                System.out.println("Файл уcпешно сохранился " + nextFileName);
                sendResponse(ServiceMessage.UPDATE + ":" + clientFile.getFileCatalog(clientUserName));
            } else {
                sendResponse(ServiceMessage.INFO + ":" + "Файл " + nextFileName + "не сохранен на сервере");
                System.out.println(nextFileName + "Не сохранился");
            }

            //Обнуляю название и хэш файла
            nextFileName = null;
            nextFileHash = null;

    }

    private synchronized void addFileToServerRequest(String fileName, String fileHash) {
        this.nextFileName = fileName;
        this.nextFileHash = fileHash;
    }

    private synchronized void sendResponse(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void login(String username, String password) {
        if (dataBase.loginRequest(username, password)) {
            //Даю имя для клиентЮзернейма, которая в дальнейшем используется как ИД
            clientUserName = username;
            String table = clientFile.getFileCatalog(clientUserName);
            sendResponse(ServiceMessage.LOGIN_ACCEPTED + ":" + table);
        } else sendResponse(ServiceMessage.LOGIN_CANCELED);
    }

    private synchronized void registration(String userName, String password) {
        if (dataBase.registration(userName, password) && clientFile.makeDir(userName)) {
            sendResponse(ServiceMessage.REGISTRATION_ACCEPTED);
        } else sendResponse(ServiceMessage.REGISTRATION_CANCELED);
    }

    private synchronized void deleteFile(String filename) {
        if (clientFile.deleteFile(clientUserName, filename)) {
            sendResponse(ServiceMessage.UPDATE + ":" + clientFile.getFileCatalog(clientUserName));
        } else {
            sendResponse(ServiceMessage.INFO + ":" + "Файл " + filename + " не удален");
            sendResponse(ServiceMessage.UPDATE + ":" + clientFile.getFileCatalog(clientUserName));
        }
    }

    //Возможно переписать и в другой класс добавить мб
    private synchronized void sendFileToClient(String fileName) {
        File file = new File("Server/clients_folder/" + clientUserName + "/" + fileName);
        sendResponse(ServiceMessage.SAVE_FILE_INFO + ":" + fileName + ":" +  clientFile.getFileMd5Hash(clientUserName, fileName));

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            sendFileInBytes(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private synchronized void sendFileInBytes(byte[] fileContent) {
        try {
            out.writeObject(fileContent);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocketsAndStreams() {
        try {
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
