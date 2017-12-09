import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
что сделать:
1. Раскидать каждое меню в отдельный класс
 */
public class ClientGui extends JFrame implements ActionListener {

    private SocketThread socketThread;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGui();
            }
        });
    }

    GridBagConstraints gbc;

    private static final String INFO_SIGN = "( i )";

    //Поля для экрана входа
    private JFrame fLogin;
    private JLabel lbInfo;
    private JTextField tfUsername;
    private JTextField tfPass;
    private JButton btnLogin;
    private JButton btnRegister;

    //Поля для экрана рагистрации
    private JFrame fReg;
    private JLabel lbInfoReg;
    private JTextField tfUsernameReg;
    private JTextField tfPassReg;
    private JTextField tfPass2Reg;
    private JButton btnRegisterReg;
    private JButton btnBackReg;

    //Основной экран папка
//    private static final int MAIN_WIDTH = 400;
//    private static final int MAIN_HEIGHT = 600;
//    private static final String MAIN_TITLE = "Web Saver";
//
    private JFrame fMain;
//    private JPanel pMain;
//    private JTable tFileTable;
//    private JPanel pMainRight;
//    private JButton btnAddFile;
//    private JButton btnRemoveFile;
//    private JButton btnSaveFile;


    LoginMenuGUI loginMenuGUI;

    public ClientGui() {
        gbc = new GridBagConstraints();
//        initLoginMenu();
        initRegMenu();
        initMainMenu();
        socketThread = new SocketThread(this);
        loginMenuGUI = new LoginMenuGUI(socketThread, this);
        socketThread.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnLogin) {
            sendLogin();
        } else if (src == btnRegister) {
            fLogin.setVisible(false);
            fReg.setVisible(true);
        } else if (src == btnRegisterReg) {
           sendForReg();
        } else if (src == btnBackReg) {
            fLogin.setVisible(true);
            fReg.setVisible(false);
        } else{
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    public void sendLogin(){
        if (!tfUsername.getText().isEmpty() && !tfPass.getText().isEmpty()) {
            String loginRequest = "login:" + tfUsername.getText() + ":" + tfPass.getText();
            socketThread.sendRequest(loginRequest);
            tfUsername.setText("");
            tfPass.setText("");
        } else lbInfo.setText("Имя или пароль пусты");
    }

    public void sendForReg(){
        if (!tfUsernameReg.getText().isEmpty() && !tfPassReg.getText().isEmpty() && !tfUsernameReg.getText().isEmpty()){
            if(tfPassReg.getText().equals(tfPass2Reg.getText())){
                String regRequest = "reg:" + tfUsernameReg.getText() + ":" + tfPassReg.getText();
                socketThread.sendRequest(regRequest);
                tfUsernameReg.setText("");
                tfPassReg.setText("");
                tfPass2Reg.setText("");
            } else setInfoReg("Пароли не совпадают");
        } else setInfoReg("Заполните все поля");
    }

//    public void setInfo(String infoMsg){
//        lbInfo.setText(INFO_SIGN + " " + infoMsg);
//    }

    public void setInfoReg(String infoMsg){
        lbInfoReg.setText(INFO_SIGN + " " + infoMsg);
    }

    public void fLoginSetVisible(boolean b){
        fLogin.setVisible(b);
    }

    public void fRegSetVisible(boolean b){
        fReg.setVisible(b);
    }

    public void fMainSetVisible(boolean b) {
        fMain.setVisible(b);
    }


    public void initLoginMenu(){
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

    public void initRegMenu(){

        final int REG_WIDTH = 400;
        final int REG_HEIGHT = 200;
        final String REG_TITLE = "Web Saver Registration";

        JPanel pReg;
        JLabel lbEmptyRowReg;
        JLabel lbUsernameReg;
        JLabel lbPassReg;
        JLabel lbPass2Reg;
        JPanel pRegB;
        JButton btnBackReg;

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

        tfPassReg = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        pReg.add(tfPassReg, gbc);

        lbPass2Reg = new JLabel("Repeat password:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        pReg.add(lbPass2Reg, gbc);

        tfPass2Reg = new JTextField(20);
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
        pRegB.add(btnRegisterReg);
        pRegB.add(btnBackReg);

        fReg.add(pRegB, BorderLayout.PAGE_END);
        fReg.setVisible(false);
    }

    public void initMainMenu(){

        final int MAIN_WIDTH = 600;
        final int MAIN_HEIGHT = 500;
        final String MAIN_TITLE = "Web Saver";

        JPanel pMain;
        JTable tFileTable;
        JPanel pMainRight;
        JButton btnAddFile;
        JButton btnRemoveFile;
        JButton btnSaveFile;

        //Основной экран программы. Работа с файлами
        fMain = new JFrame();
        fMain.setSize(MAIN_WIDTH, MAIN_HEIGHT);
        fMain.setTitle(MAIN_TITLE);
        fMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fMain.setResizable(true);
        fMain.setLocationRelativeTo(null);

        fMain.setLayout(new BorderLayout());


        //Данные для таблицы пока
        Object[] columnsHeader = new String[]{"Name", "Size"};
        Object[][] arr = new String[][]{{"file.txt", "900 kb"},
                {"image.jpg", "500 kb"},
                {"image.jpg", "500 kb"},
                {"image.jpg", "500 kb"},
                {"image.jpg", "500 kb"},
                {"image.jpg", "500 kb"}};

        pMain = new JPanel(new BorderLayout());

        JPanel pTable = new JPanel();
        tFileTable = new JTable(arr, columnsHeader);
        tFileTable.setAutoscrolls(true);
        tFileTable.setPreferredSize(new Dimension(300, 400));
        JScrollPane pane = new JScrollPane(tFileTable);
        pTable.add(pane);

        pMain.add(pTable, BorderLayout.CENTER);

        pMainRight = new JPanel(new BorderLayout());
        Box box = Box.createVerticalBox();
        btnAddFile = new JButton("Add file");
        box.add(btnAddFile);
        box.add(Box.createVerticalStrut(10));
        btnRemoveFile = new JButton("Remove file");
        box.add(btnRemoveFile);
        box.add(Box.createVerticalStrut(10));
        btnSaveFile = new JButton("Save file");
        box.add(btnSaveFile);
        pMainRight.add(box, BorderLayout.CENTER);

        fMain.add(pMain, BorderLayout.CENTER);
        fMain.add(pMainRight, BorderLayout.EAST);
//        fMain.add(pMainRight);

        fMain.setVisible(true);
    }
}

