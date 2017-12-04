import java.io.*;
import java.net.Socket;

public class SocketThread extends Thread {

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private int status;
    public Object monitor = new Object();

    public SocketThread(){
        start();
    }

    @Override
    public void run() {
        try{
            status = 0;
            Socket socket = new Socket("localhost", 8189);
            System.out.println("Подключился к серверу ");

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.flush();

            while (true){
                Object answer = new Object();
                while (true){
                    answer = in.readObject();
                    if (answer instanceof String){
                        System.out.println("Server answer: " + answer.toString());
                        answerHandler(answer.toString());
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public int getStatus() {
        return status;
    }

    public void sendRequest(String msg){
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public  void answerHandler(String answer){
        if (answer.equals("loginok")){
            status = 23;
        } else if (answer.equals("loginwrong")){
            System.out.println("Имя или пароль введены не правильно");
            status = 456;
        } else if (answer.equals("regok")){
            System.out.println("Регистрация проведена");
            status = 78;
        } else if (answer.equals("regwrong")){
            System.out.println("Данный акк зарегистрирован");
            status = 910;
        } else {
            System.out.println("Ответ сервера не известен" + answer);
        }
        synchronized (monitor){
            monitor.notifyAll();
        }
    }
}
