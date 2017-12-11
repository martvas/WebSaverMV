package Network;

import Gui.ClientGui;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Arrays;

public class SocketThreadC extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private boolean connectedToServer;
    private ClientGui clientGui;

    public SocketThreadC(ClientGui clientGui){
        this.clientGui = clientGui;
        this.connectedToServer = false;
    }

    public void tryToConnect(){
        while (!connectedToServer){
            try {
                clientGui.getLoginMenuGUI().setInfo("Пытаюсь присоединиться к серверу");
                socket = new Socket("localhost", 8189);
                connectedToServer = true;
            } catch (ConnectException e) {
                clientGui.getLoginMenuGUI().setInfo("Не смог подключиться к серверу");

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
            clientGui.getLoginMenuGUI().setInfo("Подключился к серверу");
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

    public synchronized void sendRequest(String msg){
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public synchronized void sendFile(File file){
        if (file.exists()){
            try {
                out.writeObject(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void answerHandler(String answer){
        String[] requestArr = answer.split(":");
        answer = requestArr[0];
        //--------!!!!!!Пока сюда добавлю после добавления файла-------- переделать
        if (answer.equals("loginok") || answer.equals("update")){
            clientGui.getLoginMenuGUI().setVisible(false);
            clientGui.getMainMenuGui().setVisible(true);
            //----------!!!!!!!!!!! переделать
            if(requestArr.length > 0){
                String[] arrTable = Arrays.copyOfRange(requestArr, 1, requestArr.length);
                clientGui.getMainMenuGui().setTable(arrTable);
            } else System.out.println("Net dannih iz bazy");
        } else if (answer.equals("loginwrong")){
            clientGui.getLoginMenuGUI().setInfo("Имя или пароль введены не правильно");
        } else if (answer.equals("regok")){
            clientGui.getLoginMenuGUI().setVisible(true);
            clientGui.getRegMenuGui().setVisible(false);
            clientGui.getLoginMenuGUI().setInfo("Регистрация прошла успешна. Введите данные");
        } else if (answer.equals("regwrong")){
            clientGui.getRegMenuGui().setInfoReg("Данный аккаунт уже зарегистрирован");
        } else {
            System.out.println("Ответ сервера не известен" + answer);
        }
    }
}
