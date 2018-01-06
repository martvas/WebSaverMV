package view;

import controller.MainFileController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainFilePanel extends JPanel implements ActionListener, MainFileView {

    private MainFileController mainFileController;
    private CardLayout cardLayout;

    private JLabel lbInfoMain;
    private JTable tFileTable;

    private JButton btnAddFile;
    private JButton btnRemoveFile;
    private JButton btnSaveFile;

    private JFrame parentFrame;

    public MainFilePanel(CardLayout cardLayout, JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.cardLayout = cardLayout;
        setLayout(new BorderLayout());

        JPanel pMainUp = new JPanel(new BorderLayout());
        lbInfoMain = new JLabel(" ");
        pMainUp.add(lbInfoMain, BorderLayout.LINE_START);

        JPanel pMain = new JPanel(new BorderLayout());
        JPanel pTable = new JPanel();

        tFileTable = new JTable();
        tFileTable.setAutoscrolls(true);
        JScrollPane pane = new JScrollPane(tFileTable);
        pTable.add(pane);

        pMain.add(pTable, BorderLayout.CENTER);

        JPanel pMainRight = new JPanel(new BorderLayout());
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalStrut(50));
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

        add(pMainUp, BorderLayout.NORTH);
        add(pMain, BorderLayout.CENTER);
        add(pMainRight, BorderLayout.EAST);

        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public void updateTable(String[] filesArr) {
        tFileTable.setModel(new FileTableModel(filesArr));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnAddFile) {
            getController().addFileToServer(this);
        } else if (src == btnRemoveFile) {
            getController().deleteFileFromServer(this);
        } else if (src == btnSaveFile) {
            getController().saveFileAtComputer(this);
        } else {
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    //!1!! проверить на нулл в контроллере
    @Override
    public File getFileToSendToServer() {
        File file = null;
        JFileChooser fileopen = new JFileChooser();
        int ret = fileopen.showDialog(null, "Add file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = fileopen.getSelectedFile();
        }
        return file;
    }

    @Override
    public String getFilenameFromTable() {
        String fileName = null;
        int selectedRow = tFileTable.getSelectedRow();
        if (selectedRow != -1) {
            fileName = (String) tFileTable.getValueAt(selectedRow, 0);
            System.out.println("Try to delete file: " + fileName);
        } else setInfoMsg("Выберите файл");
        return fileName;
    }

    @Override
    public String getDirectoryForFileSaving() {
        JFileChooser chooser = new JFileChooser();
        String directory = null;
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

    @Override
    public JComponent getView() {
        return this;
    }

    @Override
    public void setController(MainFileController mainFileController) {
        this.mainFileController = mainFileController;
    }

    @Override
    public MainFileController getController() {
        return mainFileController;
    }

    @Override
    public void setInfoMsg(String message) {
        lbInfoMain.setText("   " + ClientGui.INFO_SIGN + " " + message);
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
