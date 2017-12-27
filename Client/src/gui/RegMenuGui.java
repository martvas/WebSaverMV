package gui;

import network.ServerServiceThread;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegMenuGui extends JFrame implements ActionListener {
    private static final String INFO_SIGN = "( i )";

    private ServerServiceThread serverService;
    private ClientGui clientGui;
    private GridBagConstraints gbc;

    //Поля для экрана рагистрации
    private JFrame fReg;
    private JLabel lbInfoReg;
    private JTextField tfUsernameReg;
    private JTextField tfPassReg;
    private JTextField tfPass2Reg;
    private JButton btnRegisterReg;
    private JButton btnBackReg;


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnRegisterReg) {
            registrationAttempt();
        } else if (src == btnBackReg) {
            clientGui.getLoginMenuGUI().setVisible(true);
            fReg.setVisible(false);
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
                serverService.registrationRequest(userName, passwordFirst);
                tfUsernameReg.setText("");
                tfPassReg.setText("");
                tfPass2Reg.setText("");
            } else setInfoReg("Пароли не совпадают");
        } else setInfoReg("Заполните все поля");
    }

    public void setInfoReg(String infoMsg){
        lbInfoReg.setText(INFO_SIGN + " " + infoMsg);
    }

    public void setVisible(boolean b){
        fReg.setVisible(b);
    }

    public RegMenuGui(ServerServiceThread serverService, ClientGui clientGui){
        this.serverService = serverService;
        this.clientGui = clientGui;

        gbc = new GridBagConstraints();

        final int REG_WIDTH = 400;
        final int REG_HEIGHT = 200;
        final String REG_TITLE = "Web Saver Registration";

        JPanel pReg;
        JLabel lbEmptyRowReg;
        JLabel lbUsernameReg;
        JLabel lbPassReg;
        JLabel lbPass2Reg;
        JPanel pRegB;

        //Экран регистрации
        fReg = new JFrame();
        fReg.setSize(REG_WIDTH, REG_HEIGHT);
        fReg.setTitle(REG_TITLE);
        fReg.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fReg.setResizable(true);
        fReg.setLocationRelativeTo(null);

        pReg = new JPanel(new GridBagLayout());

        lbInfoReg = new JLabel(" ");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        pReg.add(lbInfoReg, gbc);

        lbEmptyRowReg = new JLabel(" ");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        pReg.add(lbEmptyRowReg, gbc);

        lbUsernameReg = new JLabel("Username: ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        pReg.add(lbUsernameReg, gbc);

        tfUsernameReg = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        pReg.add(tfUsernameReg, gbc);

        lbPassReg = new JLabel("Password: ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        pReg.add(lbPassReg, gbc);

        tfPassReg = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        pReg.add(tfPassReg, gbc);

        lbPass2Reg = new JLabel("Repeat password:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        pReg.add(lbPass2Reg, gbc);

        tfPass2Reg = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        pReg.add(tfPass2Reg, gbc);

        pReg.setBorder(new LineBorder(Color.gray));
        fReg.add(pReg, BorderLayout.CENTER);

        pRegB = new JPanel();
        btnRegisterReg = new JButton("Registration");
        btnRegisterReg.addActionListener(this);
        btnBackReg = new JButton("Back");
        btnBackReg.addActionListener(this);
        pRegB.add(btnBackReg);
        pRegB.add(btnRegisterReg);

        fReg.add(pRegB, BorderLayout.PAGE_END);
        fReg.setVisible(false);
    }

}