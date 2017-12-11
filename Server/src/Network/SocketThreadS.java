package Network;

import DataBase.DataBase;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class SocketThreadS extends Thread {
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private DataBase dataBase;

    public SocketThreadS(Socket socket, DataBase dataBase) {
        this.socket = socket;
        this.dataBase = dataBase;
        start();
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
                    if (request instanceof File) {

                        // ---------- !!! Возможно переделать
                        if ( saveFile((File) request) ){
                            sendAnswer("update:" + dataBase.getTable(getName()));
                        } else System.out.println("Не смог сохранить файл");


                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Клиент отключился");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean saveFile(File file) {
        String fileName = file.getName();
        String fileSize = Long.toString(file.length());
        String targetFileName = "Server/clients_folder/"+ getName() +"/" + fileName;
        System.out.println("Попытка сохранить файл: " + file.getName());
        FileInputStream in = null;
        FileOutputStream out = null;
        int c;

        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(targetFileName);
            while ((c = in.read()) != -1) {
                out.write(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(dataBase.addFileToDB(getName(), fileName, fileSize)){
            System.out.println("Сокет: файл " + fileName + " - инфо добавлена в базу");
        } else {
            System.out.println("Сокет: файл " + fileName + " не добавилась информация в БД");
            return false;
        }
        System.out.println("Файл сохранен в " + targetFileName);
        return true;
    }

    public synchronized void requestHandler(String request) {
        System.out.println(request);
        String[] requestArr = request.split(":");
        if (requestArr[0].equals("login")) {
            if (dataBase.loginRequest(requestArr[1], requestArr[2])) {
                String userName = requestArr[1];
                String table = dataBase.getTable(userName);
                setName(userName);
                //--------!!! Переписать
                sendAnswer("loginok:" + table);
            } else sendAnswer("loginwrong:");
        } else if (requestArr[0].equals("reg")) {
            if (dataBase.regRequrst(requestArr[1], requestArr[2])) {
                sendAnswer("regok:");
            } else sendAnswer("regwrong:");
        } else System.out.println("Сокет: Что то пошло не так с ответом");
    }

    public synchronized void sendAnswer(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
