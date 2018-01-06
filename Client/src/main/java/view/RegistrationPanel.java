package view;

import controller.RegistrationController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationPanel extends JPanel implements ActionListener, RegistrationView {

    private RegistrationController registrationController;
    private CardLayout cardLayout;

    //Поля для экрана рагистрации
    private JLabel lbInfoReg;
    private JTextField tfUsernameReg;
    private JTextField tfPassReg;
    private JTextField tfPass2Reg;
    private JButton btnRegisterReg;
    private JButton btnBackReg;
    private JFrame parentFrame;

    public RegistrationPanel(CardLayout cardLayout, JFrame parentFrame){
        this.parentFrame = parentFrame;
        this.cardLayout = cardLayout;

        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(new BorderLayout());

        JPanel pReg = new JPanel(new GridBagLayout());

        lbInfoReg = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        pReg.add(lbInfoReg, gbc);

        JLabel lbEmptyRowReg = new JLabel(" ");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        pReg.add(lbEmptyRowReg, gbc);

        JLabel lbUsernameReg = new JLabel("Username: ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        pReg.add(lbUsernameReg, gbc);

        tfUsernameReg = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        pReg.add(tfUsernameReg, gbc);

        JLabel lbPassReg = new JLabel("Password: ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        pReg.add(lbPassReg, gbc);

        tfPassReg = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        pReg.add(tfPassReg, gbc);

        JLabel lbPass2Reg = new JLabel("Repeat password:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        pReg.add(lbPass2Reg, gbc);

        tfPass2Reg = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        pReg.add(tfPass2Reg, gbc);

        add(pReg, BorderLayout.CENTER);

        JPanel pRegB = new JPanel();
        btnRegisterReg = new JButton("Registration");
        btnRegisterReg.addActionListener(this);
        btnBackReg = new JButton("Back");
        btnBackReg.addActionListener(this);
        pRegB.add(btnBackReg);
        pRegB.add(btnRegisterReg);

        add(pRegB, BorderLayout.PAGE_END);

        setBorder(new EmptyBorder(10, 10, 10, 10));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnRegisterReg) {
            registrationAttempt();
        } else if (src == btnBackReg) {
            getController().backToLoginMenu(this);
        } else{
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    public void registrationAttempt(){
        String userName = tfUsernameReg.getText();
        String passwordFirst = tfPassReg.getText();
        String passwordSecond = tfPass2Reg.getText();

        if (!userName.isEmpty() && !passwordFirst.isEmpty() && !passwordSecond.isEmpty()){
            if(passwordFirst.equals(passwordSecond)){
                getController().registrationRequest(this);
                tfUsernameReg.setText("");
                tfPassReg.setText("");
                tfPass2Reg.setText("");
            } else setInfoMsg("Пароли не совпадают");
        } else setInfoMsg("Заполните все поля");
    }

    @Override
    public String getUsername() {
        return tfUsernameReg.getText();
    }

    @Override
    public String getPassword() {
        return tfPassReg.getText();
    }

    @Override
    public JComponent getView() {
        return this;
    }

    @Override
    public void setController(RegistrationController registrationController) {
        this.registrationController = registrationController;
    }

    @Override
    public RegistrationController getController() {
        return registrationController;
    }

    @Override
    public void setInfoMsg(String message) {
        lbInfoReg.setText(ClientGui.INFO_SIGN + " " + message);
    }

    @Override
    public CardLayout getCardLayout() {
        return cardLayout;
    }

    @Override
    public JFrame getFrame() {
        return parentFrame;
    }
}
