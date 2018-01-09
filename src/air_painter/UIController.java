package air_painter;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import org.jetbrains.annotations.NotNull;

public class UIController {

    private VideoController videoController = null;

    @FXML
    private Label displayLabel = null;

    @FXML
    private ImageView imageDisplay = null;

    @FXML
    private Slider brightnessFactor = null;

    public void setVideoController(@NotNull VideoController controller) {
        this.videoController = controller;
    }

    public void setImageToDisplay(Image image) {
        if (image != null) {
            imageDisplay.setImage(image);
        }
    }

    @FXML
    private void initialize() {
        brightnessFactor.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
            videoController.setBrightnessFactor(newValue.intValue());
        });
    }

    @FXML
    private void startDisplay(ActionEvent actionEvent) {
        displayLabel.setText("Camera ON");
        videoController.startDisplay();
    }

    @FXML
    private void stopDisplay(ActionEvent actionEvent) {
        displayLabel.setText("Camera OFF");
        videoController.stopDisplay();
    }

}
