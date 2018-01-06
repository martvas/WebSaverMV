package controller;

import view.LoginView;

public interface LoginController {
    void sendLoginRequest(LoginView view);
    void goToRegistrationMenu(LoginView view);
}
