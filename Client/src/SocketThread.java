import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;

public class SocketThread extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private boolean connectedToServer;
    private ClientGui clientGui;

    public SocketThread(ClientGui clientGui){
        this.clientGui = clientGui;
        this.connectedToServer = false;
    }

    public void tryToConnect(){
        while (!connectedToServer){
            try {
                clientGui.loginMenuGUI.setInfo("Пытаюсь присоединиться к серверу");
                socket = new Socket("localhost", 8189);
                connectedToServer = true;
            } catch (ConnectException e) {
                clientGui.loginMenuGUI.setInfo("Не смог подключиться к серверу");

                //Засыпаю и попробую снова через 2 секунды
                try {
                    sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try{
            tryToConnect();
            clientGui.loginMenuGUI.setInfo("Подключился к серверу");

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

    public void sendRequest(String msg){
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void answerHandler(String answer){
        if (answer.equals("loginok")){
            clientGui.fLoginSetVisible(false);
            clientGui.fMainSetVisible(true);
        } else if (answer.equals("loginwrong")){
            clientGui.loginMenuGUI.setInfo("Имя или пароль введены не правильно");
        } else if (answer.equals("regok")){
            clientGui.fLoginSetVisible(true);
            clientGui.fLoginSetVisible(false);
            clientGui.loginMenuGUI.setInfo("Регистрация прошла успешна. Введите данные");
        } else if (answer.equals("regwrong")){
            clientGui.setInfoReg("Данный аккаунт уже зарегистрирован");
        } else {
            System.out.println("Ответ сервера не известен" + answer);
        }
    }
}
