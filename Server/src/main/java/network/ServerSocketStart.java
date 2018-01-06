package network;

import database.*;
import file.ClientFile;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketStart {

    private DataBase dataBase;
    private ClientFile clientFile;
    private static final int PORT = 8189;


    public ServerSocketStart(DataBase dataBase, ClientFile clientFile) {
        this.dataBase = dataBase;
        this.clientFile = clientFile;
    }

    public void searchClients() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен, ожидаем подключения");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new Thread(new ClientServiceThread(clientSocket, dataBase, clientFile)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
