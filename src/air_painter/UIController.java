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
    private Label cameraStatus = null;

    @FXML
    private ImageView imageDisplay = null;

    @FXML
    private Slider minObjectHeight = null;

    @FXML
    private Slider cameraBrightness = null;


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
        minObjectHeight.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
            videoController.setMinObjectHeight(newValue.doubleValue());
        });
        cameraBrightness.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
            videoController.setCameraBrightness(newValue.doubleValue() / 100.0);
        });
    }

    @FXML
    private void startDisplay(ActionEvent actionEvent) {
        cameraStatus.setText("Camera ON");
        videoController.startDisplay();
    }

    @FXML
    private void stopDisplay(ActionEvent actionEvent) {
        cameraStatus.setText("Camera OFF");
        videoController.stopDisplay();
    }

}
