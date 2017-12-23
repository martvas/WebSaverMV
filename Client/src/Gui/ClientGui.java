package Gui;

import Network.SocketThreadC;

import javax.swing.*;

/*
что сделать:
1. ПРи добавлении -  удалении файла, новое окно
 */

public class ClientGui extends JFrame {

    private SocketThreadC socketThread;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGui();
            }
        });
    }

    private LoginMenuGui loginMenuGUI;
    private RegMenuGui regMenuGui;
    private MainMenuGui mainMenuGui;

    public ClientGui() {
        socketThread = new SocketThreadC(this);
        loginMenuGUI = new LoginMenuGui(socketThread, this);
        regMenuGui = new RegMenuGui(socketThread, this);
        socketThread.start();
    }

    public void initMainMenu(String[][] fileArr){
        mainMenuGui = new MainMenuGui(socketThread, fileArr, this);
    }

    public LoginMenuGui getLoginMenuGUI() {
        return loginMenuGUI;
    }

    public RegMenuGui getRegMenuGui() {
        return regMenuGui;
    }

    public MainMenuGui getMainMenuGui() {
        return mainMenuGui;
    }
}

