package air_painter;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pawel Pluta on 06/01/18.
 */
public class VideoController {

    private static final Image noCameraDisplay;

    private FrameGrabber frameGrabber = null;

    private ObjectTracker objectTracker = null;

    private FramePainter framePainter = null;

    private UIController uiController = null;

    private ScheduledExecutorService threadExecutor = null;

    private boolean requestedVideoOutput = false;

    private boolean requestedPictureDrawing = false;

    static {
        File image = new File("./no_camera.jpg");
        noCameraDisplay = new Image(image.toURI().toString());
    }

    public VideoController(@NotNull UIController controller) {
        uiController = controller;
        objectTracker = new ObjectTracker();
        framePainter = new FramePainter();
    }

    public void startDisplay() {
        try {
            int cameraNumber = 0;
            if (frameGrabber == null) {
                frameGrabber = new FrameGrabber(cameraNumber);
            } else {
                frameGrabber.openCamera(cameraNumber);
            }
            requestedVideoOutput = true;
            startDisplayThread(0, 10, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            uiController.setImageToDisplay(noCameraDisplay);
        }
    }

    private void startDisplayThread(long initialDelay, long delay,
                                    TimeUnit unit) {
        threadExecutor = Executors.newSingleThreadScheduledExecutor();
        Runnable VideoCapture = () -> {
            Image fxImage = getImageWithDrawing();
            uiController.setImageToDisplay(fxImage);
        };
        threadExecutor.scheduleWithFixedDelay(VideoCapture, initialDelay,
                                                delay, unit);
    }

    private Image getImageWithDrawing() {
        try {
            Mat frame = frameGrabber.getNextFrame();
            Point centroid = objectTracker.getObjectCoordinates(frame);
            frame = drawOnFrame(frame, centroid);
            return FrameConverter.convertToImage(frame);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return noCameraDisplay;
        }
    }

    private Mat drawOnFrame(@NotNull Mat frame, @NotNull Point coordinates) {
        Mat result = framePainter.drawCircle(frame, coordinates);
        if (requestedPictureDrawing) {
            framePainter.addNextPoint(coordinates);
        }
        return framePainter.drawAllPoints(result);
    }

    public void stopDisplay() {
        if (requestedVideoOutput) {
            stopDisplayThread(100, TimeUnit.MILLISECONDS);
            stopCamera();
            requestedVideoOutput = false;
        }
    }

    private void stopDisplayThread(long timeout, TimeUnit unit) {
        if (threadExecutor != null && !threadExecutor.isShutdown()) {
            shutdownThread(timeout, unit);
        }
    }

    private void shutdownThread(long timeout, TimeUnit unit) {
        try {
            threadExecutor.shutdown();
            threadExecutor.awaitTermination(timeout, unit);
            threadExecutor.shutdownNow();
        } catch (InterruptedException e) {
            System.err.println("VideoController: " +
                               "Thread executor interrupted while waiting");
            e.getStackTrace();
        }
    }

    private void stopCamera() {
        if (frameGrabber != null) {
            try {
                frameGrabber.releaseCamera();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void setCameraBrightness(double brightness) {
        if (frameGrabber != null) {
            try {
                frameGrabber.setCameraBrightness(brightness);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void setMinObjectHeight(double height) {
            objectTracker.setMinContourHeight(height);
    }

    public void startDrawing() {
        if (requestedVideoOutput) {
            requestedPictureDrawing = true;
        }
    }

    public void stopDrawing() {
        if (requestedVideoOutput) {
            requestedPictureDrawing = false;
        }
    }

    public void eraseDrawing() {
        if (requestedVideoOutput) {
            framePainter.eraseAllPoints();
        }
    }

}
