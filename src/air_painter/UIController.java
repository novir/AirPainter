package air_painter;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class UIController {

    private VideoController videoController = null;

    private boolean requestedVideoOutput = false;

    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    @FXML
    private Label displayLabel = null;

    @FXML
    private ImageView imageDisplay = null;

    public void setVideoController(@NotNull VideoController controller) {
        this.videoController = controller;
    }

    public void setImageToDisplay(Image image) {
        if (image != null) {
            imageDisplay.setImage(image);
        }
    }

    @FXML
    private void startDisplay(ActionEvent actionEvent) {
        displayLabel.setText("Camera ON");
        requestedVideoOutput = true;
        videoController.startDisplay();
    }

    @FXML
    private void stopDisplay(ActionEvent actionEvent) {
        displayLabel.setText("Camera OFF");
        requestedVideoOutput = false;
        videoController.stopDisplay();
    }

}
