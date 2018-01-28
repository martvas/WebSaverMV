package view;

import controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel implements LoginView, ActionListener {

    private LoginController loginController;
    private CardLayout cardLayout;
    private JLabel lbInfo;
    private JTextField tfUsername;
    private JTextField tfPass;
    private JButton btnLogin;
    private JButton btnRegister;
    private JFrame parentFrame;

    public LoginPanel(CardLayout cardLayout, JFrame parentFrame){
        this.parentFrame = parentFrame;
        this.cardLayout = cardLayout;
        setLayout(new BorderLayout());
        JPanel pStart = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        lbInfo = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        pStart.add(lbInfo, gbc);

        JLabel lbEmptyRow = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        pStart.add(lbEmptyRow, gbc);

        JLabel lbUsername = new JLabel("Username: ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        pStart.add(lbUsername, gbc);

        tfUsername = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        pStart.add(tfUsername, gbc);

        JLabel lbPass = new JLabel("Password: ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        pStart.add(lbPass, gbc);

        tfPass = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        pStart.add(tfPass, gbc);
        add(pStart, BorderLayout.CENTER);

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(this);
        btnRegister = new JButton("Registration");
        btnRegister.addActionListener(this);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pStartB = new JPanel();
        pStartB.add(btnLogin);
        pStartB.add(btnRegister);
        add(pStartB, BorderLayout.PAGE_END);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnLogin) {
            sendLoginAttempt();
        } else if (src == btnRegister) {
            getController().goToRegistrationMenu(this);
        } else{
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    private void sendLoginAttempt(){
        if (!tfUsername.getText().isEmpty() && !tfPass.getText().isEmpty()) {
            getController().sendLoginRequest(this);
            tfUsername.setText("");
            tfPass.setText("");
        } else setInfoMsg("Имя или пароль пусты");
    }

    @Override
    public String getUserName() {
        return tfUsername.getText();
    }

    @Override
    public String getPassword() {
        return tfPass.getText();
    }

    @Override
    public JComponent getView() {
        return this;
    }

    @Override
    public void setController(LoginController loginController) {
        this.loginController = loginController;
    }

    @Override
    public LoginController getController() {
        return loginController;
    }

    @Override
    public void setInfoMsg(String message) {
        lbInfo.setText(ClientGui.INFO_SIGN + " " + message);
    }

    @Override
    public CardLayout getCardLayout() {
        return cardLayout;
    }

    @Override
    public Container getParent() {
        return super.getParent();
    }

    @Override
    public JFrame getFrame() {
        return parentFrame;
    }
}
