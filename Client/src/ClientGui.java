import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
что сделать:
1. Сделать текст на окошках где будет вывожится информация вмсето консоли
2. Нормально прерывать сессию, когда нет доступа к серверу и когда клиент отключается
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

    //Поля для экрана входа
    private static final int START_WIDTH = 400;
    private static final int START_HEIGHT = 160;
    private static final String START_TITLE = "Web Saver Login";

    private JFrame fStart;
    private JPanel pStart;
    private JLabel lbUsername;
    private JTextField tfUsername;
    private JLabel lbPass;
    private JTextField tfPass;
    private JPanel pStartB;
    private JButton btnLogin;
    private JButton btnRegister;

    //Поля для экрана рагистрации
    private static final int REG_WIDTH = 400;
    private static final int REG_HEIGHT = 160;
    private static final String REG_TITLE = "Web Saver Registration";

    private JFrame fReg;
    private JPanel pReg;
    private JLabel lbUsernameReg;
    private JTextField tfUsernameReg;
    private JLabel lbPassReg;
    private JTextField tfPassReg;
    private JLabel lbPass2Reg;
    private JTextField tfPass2Reg;
    private JPanel pRegB;
    private JButton btnRegisterReg;
    private JButton btnBackReg;

    //Основной экран папка
    private static final int MAIN_WIDTH = 400;
    private static final int MAIN_HEIGHT = 600;
    private static final String MAIN_TITLE = "Web Saver";

    private JFrame fMain;
    private JPanel pMain;
    private JTable tFileTable;
    private JPanel pMainRight;
    private JButton btnAddFile;
    private JButton btnRemoveFile;
    private JButton btnSaveFile;


    private ClientGui() {
        gbc = new GridBagConstraints();
        initLoginMenu();
        initRegMenu();
        initMainMenu();
        socketThread = new SocketThread();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnLogin) {
            if (!tfUsername.getText().isEmpty() && !tfPass.getText().isEmpty()) {
                //-----------! сделать норм общение между ClientGUi и SocketThread
                //Посылаем запрос на логин
                String loginRequest = "login:" + tfUsername.getText() + ":" + tfPass.getText();
                socketThread.sendRequest(loginRequest);
                tfUsername.setText("");
                tfPass.setText("");

                //жду ответа
                synchronized (socketThread.monitor){
                    try {
                        socketThread.monitor.wait();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

                //Обрабатываю статус который изменился после ответа
                if (socketThread.getStatus() == 23){
                    fStart.setVisible(false);
                    fMain.setVisible(true);
                } else if (socketThread.getStatus() == 456){
                    System.out.println("Данные введены не верно");
                } else {
                    System.out.println("Ответ сервера неизвестен. Статус: " + socketThread.getStatus());
                }

            } else System.out.println("Введите логин и пароль");
        } else if (src == btnRegister) {
            fStart.setVisible(false);
            fReg.setVisible(true);
        } else if (src == btnRegisterReg) {
            if (!tfUsernameReg.getText().isEmpty() && !tfPassReg.getText().isEmpty() && !tfUsernameReg.getText().isEmpty()){
                if(tfPassReg.getText().equals(tfPass2Reg.getText())){

                    String regRequest = "reg:" + tfUsernameReg.getText() + ":" + tfPassReg.getText();
                    socketThread.sendRequest(regRequest);
                    tfUsernameReg.setText("");
                    tfPassReg.setText("");
                    tfPass2Reg.setText("");

                    synchronized (socketThread.monitor){
                        try {
                            socketThread.monitor.wait();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (socketThread.getStatus() == 78){
                        System.out.println("Регистрация прошла успешна");
                        fStart.setVisible(true);
                        fReg.setVisible(false);
                    } else if (socketThread.getStatus() == 910){
                        System.out.println("Данное имя уже зарегистрировано");
                    }
                } else System.out.println("Пароли не совпадают");
            } else System.out.println("Заполните все поля");
        } else if (src == btnBackReg) {
            fStart.setVisible(true);
            fReg.setVisible(false);
        } else{
            throw new RuntimeException("Unknown src = " + src);
        }
    }


    public void initLoginMenu(){
        //Экран входа
        fStart = new JFrame();
        fStart.setSize(START_WIDTH, START_HEIGHT);
        fStart.setTitle(START_TITLE);
        fStart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fStart.setResizable(true);
        fStart.setLocationRelativeTo(null);

        pStart = new JPanel(new GridBagLayout());
        gbc.fill = GridBagConstraints.BOTH;

        lbUsername = new JLabel("Username: ");
        gbc.gridx = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        pStart.add(lbUsername, gbc);

        tfUsername = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        pStart.add(tfUsername, gbc);

        lbPass = new JLabel("Password: ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        pStart.add(lbPass, gbc);

        tfPass = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        pStart.add(tfPass, gbc);
        pStart.setBorder(new LineBorder(Color.gray));
        fStart.add(pStart, BorderLayout.CENTER);

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(this);
        btnRegister = new JButton("Registration");
        btnRegister.addActionListener(this);
        pStartB = new JPanel();
        pStartB.add(btnLogin);
        pStartB.add(btnRegister);
        fStart.add(pStartB, BorderLayout.PAGE_END);
        fStart.setVisible(true);
    }

    public void initRegMenu(){
        //Экран регистрации
        fReg = new JFrame();
        fReg.setSize(REG_WIDTH, REG_HEIGHT);
        fReg.setTitle(REG_TITLE);
        fReg.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fReg.setResizable(true);
        fReg.setLocationRelativeTo(null);

        pReg = new JPanel(new GridBagLayout());

        lbUsernameReg = new JLabel("Username: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        pReg.add(lbUsernameReg, gbc);

        tfUsernameReg = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        pReg.add(tfUsernameReg, gbc);

        lbPassReg = new JLabel("Password: ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        pReg.add(lbPassReg, gbc);

        tfPassReg = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        pReg.add(tfPassReg, gbc);

        lbPass2Reg = new JLabel("Repeat password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        pReg.add(lbPass2Reg, gbc);

        tfPass2Reg = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
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
                {"image.jpg", "500 kb"}};

        pMain = new JPanel(new BorderLayout());

        tFileTable = new JTable(arr, columnsHeader);
        pMain.add(tFileTable, BorderLayout.CENTER);

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

        fMain.setVisible(false);
    }
}

