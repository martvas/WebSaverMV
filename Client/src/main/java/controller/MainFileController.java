package controller;

import view.MainFileView;

public interface MainFileController {
    void addFileToServer(MainFileView view);
    void deleteFileFromServer(MainFileView view);
    void saveFileAtComputer(MainFileView view);
}
