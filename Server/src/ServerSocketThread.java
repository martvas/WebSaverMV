import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
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
