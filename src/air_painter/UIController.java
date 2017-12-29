package air_painter;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class UIController {

    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    @FXML
    private Label displayLabel;

    @FXML
    private ImageView imageDisplay;


    public UIController() {

    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void startDisplay(ActionEvent actionEvent) {
        displayLabel.setText("Start");
    }
}
