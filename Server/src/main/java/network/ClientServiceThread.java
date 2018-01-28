package network;

import authorization.Database;
import authorization.User;
import file.ClientFile;
import library.ServiceMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientServiceThread implements Runnable {
    private final Socket socket;
    private User user;
    private ClientFile clientFile;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Database database;

    public ClientServiceThread(Socket socket, Database database) {
        this.socket = socket;
        this.database = database;
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
                        clientFile.tryToSaveFile((byte[]) request);
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Клиент отключился");
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
            case ServiceMessage.REGISTRATION:
                registration(requestArr[1], requestArr[2]);
                break;
            case ServiceMessage.ADD_FILE:
                clientFile.addFileToServerRequest(requestArr[1], requestArr[2]);
                break;
            case ServiceMessage.DELETE_FILE:
                clientFile.tryToDeleteFile(requestArr[1]);
                break;
            case ServiceMessage.SAVE_FILE:
                clientFile.sendFileToClient(requestArr[1]);
                break;
            default:
                System.out.println("Неизвестный тип запроса от клиента");
                break;
        }
    }

    public synchronized void sendResponse(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
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

    private synchronized void login(String username, String password) {
        if (database.loginRequestToDb(username, password)) {
            user = new User(username);
            clientFile = new ClientFile(this, user);
            sendResponse(ServiceMessage.LOGIN_ACCEPTED + ":" + clientFile.getFileCatalog());
        } else sendResponse(ServiceMessage.LOGIN_CANCELED);
    }

    private synchronized void registration(String userName, String password) {
        if (database.registrationRequestToDb(userName, password) && ClientFile.makeDir(userName)) {
            sendResponse(ServiceMessage.REGISTRATION_ACCEPTED);
        } else sendResponse(ServiceMessage.REGISTRATION_CANCELED);
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
