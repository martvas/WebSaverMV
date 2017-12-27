package gui;

import network.ServerServiceThread;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginMenuGui extends JFrame implements ActionListener {

    private static final String INFO_SIGN = "( i )";

    private ServerServiceThread serverService;
    private ClientGui clientGui;

    //Поля для экрана входа
    private JFrame fLogin;
    private JLabel lbInfo;
    private JTextField tfUsername;
    private JTextField tfPass;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginMenuGui(ServerServiceThread serverService, ClientGui clientGui){
        this.serverService = serverService;
        this.clientGui = clientGui;

        GridBagConstraints gbc = new GridBagConstraints();

        //Экран входа
        final int START_WIDTH = 400;
        final int START_HEIGHT = 160;
        final String START_TITLE = "Web Saver Login";

        JPanel pStart;
        JLabel lbEmptyRow;
        JLabel lbUsername;
        JLabel lbPass;
        JPanel pStartB;

        fLogin = new JFrame();
        fLogin.setSize(START_WIDTH, START_HEIGHT);
        fLogin.setTitle(START_TITLE);
        fLogin.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fLogin.setResizable(true);
        fLogin.setLocationRelativeTo(null);

        pStart = new JPanel(new GridBagLayout());
        gbc.fill = GridBagConstraints.BOTH;

        lbInfo = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        pStart.add(lbInfo, gbc);

        lbEmptyRow = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        pStart.add(lbEmptyRow, gbc);

        lbUsername = new JLabel("Username: ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        pStart.add(lbUsername, gbc);

        tfUsername = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        pStart.add(tfUsername, gbc);

        lbPass = new JLabel("Password: ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        pStart.add(lbPass, gbc);

        tfPass = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        pStart.add(tfPass, gbc);
        pStart.setBorder(new LineBorder(Color.gray));
        fLogin.add(pStart, BorderLayout.CENTER);

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(this);
        btnRegister = new JButton("Registration");
        btnRegister.addActionListener(this);
        pStartB = new JPanel();
        pStartB.add(btnLogin);
        pStartB.add(btnRegister);
        fLogin.add(pStartB, BorderLayout.PAGE_END);
        fLogin.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnLogin) {
            sendLogin();
        } else if (src == btnRegister) {
            fLogin.setVisible(false);
            clientGui.getRegMenuGui().setVisible(true);
        } else{
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    public void sendLogin(){
        if (!tfUsername.getText().isEmpty() && !tfPass.getText().isEmpty()) {
            serverService.loginRequest(tfUsername.getText(), tfPass.getText());
            tfUsername.setText("");
            tfPass.setText("");
        } else lbInfo.setText("Имя или пароль пусты");
    }

    public void setInfo(String infoMsg){
        lbInfo.setText(INFO_SIGN + " " + infoMsg);
    }

    public void setVisible(boolean b){
        fLogin.setVisible(b);
    }

}
