import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerSocketThread extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private DataBase dataBase;
    private Socket socket;


    public ServerSocketThread(DataBase dataBase){
        this.dataBase = dataBase;
        start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен, ожидаем подключения");
            socket = serverSocket.accept();

            System.out.println("Клиент подключился");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.flush();

            while (true){
                Object request = new Object();
                while (true){
                    request = in.readObject();
                    if (request instanceof String){
                        requestHandler(request.toString());
                    }

                    if (request instanceof File){
                        saveFile((File) request);
                    }
                }
            }
        } catch (SocketException e){
            System.out.println("Клиент отключился");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveFile(File file) {
        String fileName = file.getName();
        String targetFileName = "Server/clients_folder/martin123/" + fileName;
        System.out.println("Попытка сохранить файл: " + file.getName());
        FileInputStream in = null;
        FileOutputStream out = null;
        int c;

        try {

            in = new FileInputStream(file);
            out = new FileOutputStream(targetFileName);
            while ((c = in.read()) != -1){
                out.write(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("Файл сохранен в " + targetFileName);
    }

    public synchronized void requestHandler(String request){
        System.out.println(request);
        String[] requestArr = request.split(":");
        if(requestArr[0].equals("login")){
            if(dataBase.loginRequest(requestArr[1], requestArr[2])){
                sendAnswer("loginok");
            } else sendAnswer("loginwrong");
        }else if(requestArr[0].equals("reg")){
            if (dataBase.regRequrst(requestArr[1], requestArr[2])){
                sendAnswer("regok");
            } else sendAnswer("regwrong");
        } else System.out.println("Chet ne poshlo");
    }

    public synchronized void sendAnswer(String msg){
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
