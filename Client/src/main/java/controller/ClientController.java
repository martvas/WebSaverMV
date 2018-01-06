package controller;

import model.network.ServerServiceThread;
import view.ClientGui;
import view.LoginView;
import view.MainFileView;
import view.RegistrationView;

import java.io.File;

public class ClientController implements LoginController, RegistrationController, MainFileController {
    private ServerServiceThread serverService;

    public ClientController(ServerServiceThread serverService) {
        this.serverService = serverService;
    }

    //LoginController methods
    @Override
    public void sendLoginRequest(LoginView view) {
        serverService.loginRequest(view.getUserName(), view.getPassword());
    }

    @Override
    public void goToRegistrationMenu(LoginView view) {
        view.getCardLayout().show(view.getParent(), ClientGui.REGISTRATION_VIEW);
        view.getFrame().pack();
    }

    //RegistrationController methods
    @Override
    public void registrationRequest(RegistrationView view) {
        serverService.registrationRequest(view.getUsername(), view.getPassword());
    }

    @Override
    public void backToLoginMenu(RegistrationView view) {
        view.getCardLayout().show(view.getParent(), ClientGui.LOGIN_VIEW);
        view.getFrame().pack();
    }

    //MainFileController methods
    @Override
    public void addFileToServer(MainFileView view) {
        File temp = view.getFileToSendToServer();
        if (temp == null) {
            view.setInfoMsg("Вы не выбрали ни одного файла");
        } else serverService.addFileToServer(temp);
    }

    @Override
    public void deleteFileFromServer(MainFileView view) {
        String temp = view.getFilenameFromTable();
        if (temp == null) {
            view.setInfoMsg("Выберите файл из списка");
        } else serverService.deleteFileRequest(temp);
    }

    @Override
    public void saveFileAtComputer(MainFileView view) {
        serverService.saveFileRequest(view.getFilenameFromTable(), view.getDirectoryForFileSaving());
    }

}
