package view;

import javax.swing.*;
import java.awt.*;

public interface View<Controller> {
    //для cardLayout добавлении view
    JComponent getView();
    void setController(Controller controller);

    //Получить контроллер чтобы вызвать методы из контроллера
    Controller getController();

    void setInfoMsg(String message);

    //Чтобы переключать вью в контроллере
    CardLayout getCardLayout();

    //Чтобы переключать вью в контроллере
    Container getParent();

    //Для того чтобы менять размеры Jpanel
    JFrame getFrame();
}
