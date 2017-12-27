package gui;

import network.ServerServiceThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/*
Что сделать:
1ю Вынести выбор файла в отдельный метод и проверять выбран ли файл

 */

public class MainMenuGui extends JFrame implements ActionListener {

    private ServerServiceThread serverService;
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


    public MainMenuGui(ServerServiceThread serverService, String[][] filesArr, ClientGui clientGui) {
        this.serverService = serverService;
        this.clientGui = clientGui;

        //Основной экран программы. Работа с файлами
        this.filesArr = filesArr;

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

        tFileTable = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };
        tFileTable.setAutoscrolls(true);
        tFileTable.setPreferredSize(new Dimension(300, 400));
        tFileTable.setModel(new FileTableModel(filesArr));
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
            saveFile();
        } else {
            throw new RuntimeException("Unknown src = " + src);
        }
    }


    public void addFile(){
        JFileChooser fileopen = new JFileChooser();
        int ret = fileopen.showDialog(null, "Add file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            String fileName = file.getName();
            String fileSize = String.valueOf(file.length());
            serverService.addFileRequest(fileName, fileSize);

            try {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                serverService.sendFileInBytes(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTable(String[][] filesArr){
        tFileTable.setModel(new FileTableModel(filesArr));
    }

    public void removeFile(){
        int selectedRow = tFileTable.getSelectedRow();
        String fileName = (String) tFileTable.getValueAt(selectedRow, 0);
        String fileSize = (String) tFileTable.getValueAt(selectedRow, 1);

        System.out.println("Try to delete file: " + fileName + " " + fileSize);
        serverService.deleteFileRequest(fileName, fileSize);
    }

    public void saveFile(){
        int selectedRow = tFileTable.getSelectedRow();
        String fileName = (String) tFileTable.getValueAt(selectedRow, 0);
        String fileSize = (String) tFileTable.getValueAt(selectedRow, 1);
        System.out.println("Try to download file: " + fileName + " " + fileSize);
        serverService.saveFileRequest(fileName, fileSize);
    }

    public String getDirectoryToSaveFile(){
        JFileChooser chooser = new JFileChooser();
        String directory = null;
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose directory to save file");

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showDialog(null, "Save file here") == JFileChooser.APPROVE_OPTION) {
            directory = chooser.getSelectedFile().toString();
        } else {
            System.out.println("No Selection ");
        }
        return directory;
    }
}
