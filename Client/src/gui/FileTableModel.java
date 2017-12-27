package gui;

import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {

    private String[][] filesArr;
    private String[] columnsNames = {"Name", "Size"};

    public FileTableModel(String[][] filesArr) {
        this.filesArr = filesArr;
    }

    @Override
    public int getRowCount() {
        return filesArr.length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        return columnsNames[column];

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return filesArr[rowIndex][columnIndex];
    }


}
