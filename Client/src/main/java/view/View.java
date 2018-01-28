package view;

import javax.swing.*;
import java.awt.*;

public interface View<Controller> {
    //для использования данной вююшки в cardLayout
    JComponent getView();

    void setController(Controller controller);

    //Получить контроллер чтобы вызвать методы из контроллера, по нажатию кнопок
    Controller getController();

    void setInfoMsg(String message);

    //Чтобы переключать вью в контроллере
    CardLayout getCardLayout();

    //Чтобы переключать вью в контроллере
    Container getParent();

    //Для того чтобы менять размеры Jpanel при переключении
    JFrame getFrame();
}
