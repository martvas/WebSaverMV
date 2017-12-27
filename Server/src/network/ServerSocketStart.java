package network;

import database.*;
import fileWork.FileWork;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketStart {

    private DataBase dataBase;
    private Socket clientSocket;
    private FileWork fileWork;


    public ServerSocketStart(DataBase dataBase, FileWork fileWork) {
        this.dataBase = dataBase;
        this.fileWork = fileWork;
    }

    public void searchClients() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен, ожидаем подключения");

            while (true) {
                clientSocket = serverSocket.accept();
                new Thread(new ClientServiceThread(clientSocket, dataBase, fileWork)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
