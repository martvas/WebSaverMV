import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainMenuGui extends JFrame implements ActionListener {

    private SocketThread socketThread;
    private ClientGui clientGui;

    //Основной экран папка
    private static final int MAIN_WIDTH = 600;
    private static final int MAIN_HEIGHT = 500;
    private static final String MAIN_TITLE = "Web Saver";

    private JFrame fMain;
    private JPanel pMain;
    private JLabel lbInfoMain;
    private JTable tFileTable;
    private JPanel pMainRight;
    private JButton btnAddFile;
    private JButton btnRemoveFile;
    private JButton btnSaveFile;


    public MainMenuGui(SocketThread socketThread, ClientGui clientGui) {
        this.socketThread = socketThread;
        this.clientGui = clientGui;
        //Основной экран программы. Работа с файлами
        fMain = new JFrame();
        fMain.setSize(MAIN_WIDTH, MAIN_HEIGHT);
        fMain.setTitle(MAIN_TITLE);
        fMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fMain.setResizable(true);
        fMain.setLocationRelativeTo(null);

        fMain.setLayout(new BorderLayout());

        JPanel pMainUp = new JPanel(new BorderLayout());
        lbInfoMain = new JLabel(" ");
        pMainUp.add(lbInfoMain, BorderLayout.LINE_START);

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
        btnAddFile.addActionListener(this);
        box.add(btnAddFile);
        box.add(Box.createVerticalStrut(10));
        btnRemoveFile = new JButton("Remove file");
        btnRemoveFile.addActionListener(this);
        box.add(btnRemoveFile);
        box.add(Box.createVerticalStrut(10));
        btnSaveFile = new JButton("Save file");
        btnSaveFile.addActionListener(this);
        box.add(btnSaveFile);
        pMainRight.add(box, BorderLayout.CENTER);


        fMain.add(pMainUp, BorderLayout.NORTH);
        fMain.add(pMain, BorderLayout.CENTER);
        fMain.add(pMainRight, BorderLayout.EAST);

        fMain.setVisible(false);
    }

    public void setVisible(boolean b) {
        fMain.setVisible(b);
    }

    public void setInfo(String infoMsg) {
        lbInfoMain.setText("     " + infoMsg);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnAddFile) {
            addFile();
        } else if (src == btnRemoveFile) {

        } else if (src == btnSaveFile) {

        } else {
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    public void addFile(){
        JFileChooser fileopen = new JFileChooser();
        int ret = fileopen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            setInfo(file.getName());
            socketThread.sendFile(file);
        }
    }
}
