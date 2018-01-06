package air_painter;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

public class UIController {

    private ScheduledExecutorService executor = null;

    private VideoGrabber videoGrabber = null;

    private boolean requestedVideoOutput = false;

    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    @FXML
    private Label displayLabel = null;

    @FXML
    private ImageView imageDisplay = null;

    @FXML
    private void startDisplay(ActionEvent actionEvent) {
        displayLabel.setText("Camera ON");
        requestedVideoOutput = true;
        videoGrabber = new VideoGrabber(0);
        startDisplayThread(0, 10, TimeUnit.MILLISECONDS);
    }

    private void startDisplayThread(long initialDelay, long replyPeriod,
                                    TimeUnit unit) {
        executor = Executors.newSingleThreadScheduledExecutor();
        Runnable videoCaptureInThread = () -> {
            if (requestedVideoOutput) {
                Image fxImage = videoGrabber.getNextFrameAsImage();
                imageDisplay.setImage(fxImage);
            }
        };
        executor.scheduleAtFixedRate(videoCaptureInThread, initialDelay,
                                     replyPeriod, unit);
    }

    @FXML
    private void stopDisplay(ActionEvent actionEvent) {
        displayLabel.setText("Camera OFF");
        requestedVideoOutput = false;
        stopDisplayThread(100, TimeUnit.MILLISECONDS);
        videoGrabber.releaseCamera();
    }

    private void stopDisplayThread(long timeout, TimeUnit unit) {
        if (executor != null && !executor.isShutdown()) {
            shutdownThread(timeout, unit);
        }
    }

    private void shutdownThread(long timeout, TimeUnit unit) {
        try {
            executor.shutdown();
            executor.awaitTermination(timeout, unit);
            executor.shutdownNow();
        } catch (InterruptedException e) {
            System.err.println("Thread executor interrupted while waiting");
            e.getStackTrace();
        }
    }

    public void stopDisplay() {
        if (requestedVideoOutput) {
            requestedVideoOutput = false;
            shutdownThread(100, TimeUnit.MILLISECONDS);
            videoGrabber.releaseCamera();
        }
    }

}
