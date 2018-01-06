package controller;

import view.RegistrationView;

public interface RegistrationController {
    void registrationRequest(RegistrationView view);
    void backToLoginMenu(RegistrationView view);
}
