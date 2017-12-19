package Gui;

import Network.SocketThreadC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainMenuGui extends JFrame implements ActionListener {

    private SocketThreadC socketThread;
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

    private String[][] filesArr;


    public MainMenuGui(SocketThreadC socketThread, String[][] filesArr, ClientGui clientGui) {
        this.socketThread = socketThread;
        this.clientGui = clientGui;

        this.filesArr = filesArr;
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

        Object[] columnNames = new String[]{"Name", "Size"};
        pMain = new JPanel(new BorderLayout());

        JPanel pTable = new JPanel();
        tFileTable = new JTable(filesArr, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };
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

        fMain.setVisible(true);
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
            removeFile();
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



    //Обновление таблицы
    public void updateTable(String[][] filesArr){
        this.filesArr = filesArr;
        for (int i = 0; i < filesArr.length; i++) {
            for (int j = 0; j < 2; j++) {
                tFileTable.setValueAt(filesArr[i][j], i, j);
            }
        }
    }

    public String[][] getFilesArr() {
        return filesArr;
    }

    public void removeFile(){
        int selectedRow = tFileTable.getSelectedRow();
        String fileName = (String) tFileTable.getValueAt(selectedRow, 0);
        String fileSize = (String) tFileTable.getValueAt(selectedRow, 1);
        System.out.println("Try to delete file: " + fileName + " " + fileSize);
        socketThread.sendRequest("deletefile:" + fileName + ":" + fileSize);

    }


}
