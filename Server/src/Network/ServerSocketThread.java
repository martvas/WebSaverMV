package Network;
import DataBase.*;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketThread extends Thread {

    private DataBase dataBase;
    private Socket clientSocket;


    public ServerSocketThread(DataBase dataBase){
        this.dataBase = dataBase;
        start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен, ожидаем подключения");

            while (true){
                clientSocket = serverSocket.accept();
                new ClientServiceThread(clientSocket, dataBase);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
