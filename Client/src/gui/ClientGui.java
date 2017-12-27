package gui;

import network.ServerServiceThread;

import javax.swing.*;

/*
что сделать(для меня коменты):
2. Проверить инкапсуляцию для методов и полей + сервер
 */

public class ClientGui extends JFrame {

    private ServerServiceThread serverServiceThread;

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
        serverServiceThread = new ServerServiceThread(this);
        loginMenuGUI = new LoginMenuGui(serverServiceThread, this);
        regMenuGui = new RegMenuGui(serverServiceThread, this);
        serverServiceThread.start();
    }

    public void initMainMenu(String[][] fileArr){
        mainMenuGui = new MainMenuGui(serverServiceThread, fileArr, this);
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

