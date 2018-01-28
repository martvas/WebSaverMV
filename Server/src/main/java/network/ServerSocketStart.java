package network;

import authorization.Database;
import library.CommonInfo;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketStart {

    private Database database;


    public ServerSocketStart(Database database) {
        this.database = database;
    }

    public void searchClients() {
        try (ServerSocket serverSocket = new ServerSocket(CommonInfo.PORT)) {
            System.out.println("Сервер запущен, ожидаем подключения");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new Thread(new ClientServiceThread(clientSocket, database)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
