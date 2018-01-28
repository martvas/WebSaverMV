package view;

import controller.ClientController;
import library.ServiceMessage;
import network.ServerServiceListener;
import network.ServerServiceThread;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ClientGui extends JFrame implements ServerServiceListener {

    public static final String INFO_SIGN = "( i )";
    public static final String LOGIN_VIEW = "View.login";
    public static final String REGISTRATION_VIEW = "View.registration";
    public static final String MAIN_FILE_VIEW = "View.mainfileview";

    private JPanel corePanel;
    private LoginView loginPanel;
    private RegistrationView registrationPanel;
    private MainFilePanel mainFilePanel;

    private CardLayout cardLayout;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGui();
            }
        });
    }

    private ServerServiceThread serverService = new ServerServiceThread(this);
    private ClientController clientController = new ClientController(serverService);

    public ClientGui(){
        setTitle("WebSaver");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        corePanel = new JPanel();
        cardLayout = new MyCardLayout();

        corePanel.setLayout(cardLayout);
        loginPanel = new LoginPanel(cardLayout, this);
        registrationPanel = new RegistrationPanel(cardLayout, this);
        mainFilePanel = new MainFilePanel(cardLayout, this);

        corePanel.add(loginPanel.getView(), LOGIN_VIEW);
        corePanel.add(registrationPanel.getView(), REGISTRATION_VIEW);
        corePanel.add(mainFilePanel.getView(), MAIN_FILE_VIEW);
        loginPanel.setController(clientController);
        registrationPanel.setController(clientController);
        mainFilePanel.setController(clientController);
        add(corePanel);

        setVisible(true);
        setLocationRelativeTo(null);
        setResizable(true);

        cardLayout.show(corePanel, LOGIN_VIEW);
        pack();
        serverService.start();
    }

    @Override
    public void serverResponse(ServerServiceThread serverService, String responseMsg) {
        String[] responseArr = responseMsg.split(":");
        String responseID = responseArr[0];

        switch (responseID) {
            case (ServiceMessage.TRY_TO_CONNECT):
                loginPanel.setInfoMsg("Пытаюсь присоединиться к серверу");
                break;
            case (ServiceMessage.NO_CONNECT):
                loginPanel.setInfoMsg("Не смог подключиться к серверу");
                break;
            case (ServiceMessage.CONNECTED_TO_SERVER):
                loginPanel.setInfoMsg("Подключился к серверу");
                break;
            case (ServiceMessage.LOGIN_CANCELED):
                loginPanel.setInfoMsg("Имя или пароль введены не правильно");
                break;
            case (ServiceMessage.REGISTRATION_ACCEPTED):
                loginPanel.setInfoMsg("Регистрация прошла успешна. Введите данные");
                cardLayout.show(corePanel, LOGIN_VIEW);
                pack();
                break;
            case (ServiceMessage.REGISTRATION_CANCELED):
                registrationPanel.setInfoMsg("Данный аккаунт уже зарегистрирован");
                break;
            case (ServiceMessage.LOGIN_ACCEPTED):
                loginAccepted(responseArr);
                break;
            case (ServiceMessage.UPDATE):
                sendArrToUpdateTable(responseArr);
                break;
            case (ServiceMessage.FILE_SAVED):
                mainFilePanel.setInfoMsg(responseArr[1] + ":" + responseArr[2]);
                break;
            case (ServiceMessage.MAIN_INFO):
                mainFilePanel.setInfoMsg(responseArr[1]);
                break;
            case (ServiceMessage.CONNECTION_LOST):
                closeConnectionDialog();
            default:
                System.out.println("Ответ сервера не известен" + responseMsg);
                break;
        }
    }

    private void closeConnectionDialog() {
        Object[] options = {"OK"};
        int n = JOptionPane.showOptionDialog(null,
                "Сервер прервал соединение","WebSaver",
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);
        System.exit(0);
    }

    //Если произошел успешный логин
    public synchronized void loginAccepted(String[] responseArr) {
        cardLayout.show(corePanel, MAIN_FILE_VIEW);
        pack();
        sendArrToUpdateTable(responseArr);
    }

    private void sendArrToUpdateTable(String[] responseArr) {
        if (responseArr.length > 0) {
            String[] filesArr = Arrays.copyOfRange(responseArr, 1, responseArr.length);
            mainFilePanel.updateTable(filesArr);
        } else System.out.println("У пользователя нету файлов");
    }

}
