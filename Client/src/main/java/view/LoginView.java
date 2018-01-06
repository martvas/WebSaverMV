package view;

import controller.LoginController;

public interface LoginView extends View<LoginController> {
    String getUserName();
    String getPassword();
}
