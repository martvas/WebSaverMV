package view;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;

public class FileTableModel extends AbstractTableModel {

    private String[][] filesArr;
    private String[] columns = {"Name", "Size"};
    private DecimalFormat decimal = new DecimalFormat("0.00");


    public FileTableModel(String[] filesArr) {
        this.filesArr = getFiles2dArr(filesArr);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
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
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return filesArr[rowIndex][columnIndex];
    }

    //Преобразует одномерный массив полученный с сервера, в двумерный (для таблицы) и преобразует байты в Кб
    private String[][] getFiles2dArr(String[] stringArr) {
        int numCell = 0;
        int rows = stringArr.length / columns.length;
        String[][] string2dArr = new String[rows][columns.length];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns.length; j++) {
                if (j == 1) {
                    long temp = Long.parseLong(stringArr[numCell++]);
                    string2dArr[i][j] = bytesToNormalSize(temp);
                } else string2dArr[i][j] = stringArr[numCell++];
            }
        }
        return string2dArr;
    }

    private String bytesToNormalSize(long bytes) {
        String fileSize;

        if (bytes > 1024 && bytes < 1048576) {
            float tempByteCnt = (float)bytes / 1024;
            fileSize = decimal.format(tempByteCnt) + " Kb";
        } else if (bytes > 1048576) {
            float tempByteCnt = (float)bytes / 1048576;
            fileSize = decimal.format(tempByteCnt) + " Mb";
        } else fileSize = bytes + " Bytes";
        return fileSize;
    }
}
