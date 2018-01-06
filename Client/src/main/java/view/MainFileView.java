package view;

import controller.MainFileController;

import java.io.File;

public interface MainFileView extends View<MainFileController> {
    File getFileToSendToServer();
    String getFilenameFromTable();
    String getDirectoryForFileSaving();
}
