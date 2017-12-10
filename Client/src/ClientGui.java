import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
что сделать:

 */
public class ClientGui extends JFrame {

    private SocketThread socketThread;

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
        socketThread = new SocketThread(this);
        System.out.println("potok " + socketThread.getName());
        loginMenuGUI = new LoginMenuGui(socketThread, this);
        regMenuGui = new RegMenuGui(socketThread, this);
        mainMenuGui = new MainMenuGui(socketThread, this);
        socketThread.start();
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

