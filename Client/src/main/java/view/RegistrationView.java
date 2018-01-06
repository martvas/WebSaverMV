package view;

import controller.RegistrationController;

public interface RegistrationView extends View<RegistrationController> {
    String getUsername();
    String getPassword();
}
