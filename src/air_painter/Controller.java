package air_painter;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

public class Controller {

    public Label introMessage;

    public void printIntroInfo(ActionEvent actionEvent) {
        introMessage.setText("Start !");
    }
}
