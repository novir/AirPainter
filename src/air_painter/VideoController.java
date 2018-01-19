package air_painter;

import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pawel Pluta on 06/01/18.
 */
public class VideoController {

    private final Image noCameraDisplay;

    private VideoGrabber videoGrabber = null;

    private ImagePainter imagePainter = null;

    private UIController uiController = null;

    private ScheduledExecutorService threadExecutor = null;

    private double minObjectHeight = 25.0;

    private boolean requestedVideoOutput = false;

    private boolean requestedPictureDrawing = false;

    public static void loadOpenCVLibrary() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public VideoController(@NotNull UIController controller) {
        loadOpenCVLibrary();
        this.uiController = controller;
        imagePainter = new ImagePainter();
        File image = new File("./no_camera.jpg");
        noCameraDisplay = new Image(image.toURI().toString());
    }

    public void startDisplay() {
        try {
            int cameraNumber = 0;
            if (videoGrabber == null) {
                videoGrabber = new VideoGrabber(cameraNumber);
            } else {
                videoGrabber.openCamera(cameraNumber);
            }
            requestedVideoOutput = true;
            startDisplayThread(0, 1, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            uiController.setImageToDisplay(noCameraDisplay);
            System.err.println(e.getMessage());
        }
    }

    private void startDisplayThread(long initialDelay, long replyPeriod,
                                    TimeUnit unit) {
        threadExecutor = Executors.newSingleThreadScheduledExecutor();
        Runnable videoCaptureInThread = () -> {
            if (requestedVideoOutput) {
                Image fxImage = getImageWithDrawing();
                uiController.setImageToDisplay(fxImage);
            }
        };
        threadExecutor.scheduleAtFixedRate(videoCaptureInThread, initialDelay,
                                           replyPeriod, unit);
    }

    private Image getImageWithDrawing() {
        if (videoGrabber != null) {
            try {
                Mat frame = videoGrabber.getNextFrame();
                Point centroid = getTrackedObjectCoordinates(frame);
                drawOnFrame(frame, centroid);
                return FrameConverter.convertToImage(frame);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return noCameraDisplay;
    }

    @NotNull
    private Point getTrackedObjectCoordinates(@NotNull Mat originalFrame) {
        Mat blueObject = VideoProcessor.findBlueObject(originalFrame);
        List<MatOfPoint> contours =
                VideoProcessor.findBigContours(blueObject, minObjectHeight);
        return VideoProcessor.calculateAvgCentroid(contours);
    }

    private void drawOnFrame(@NotNull Mat frame, @NotNull Point coordinates) {
        imagePainter.drawCircle(frame, coordinates);
        if (requestedPictureDrawing) {
            imagePainter.addNextPoint(coordinates);
        }
        imagePainter.drawAllPoints(frame);
    }

    public void stopDisplay() {
        if (requestedVideoOutput) {
            stopDisplayThread(10, TimeUnit.MILLISECONDS);
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
        if (videoGrabber != null) {
            try {
                videoGrabber.releaseCamera();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void setCameraBrightness(double brightness) {
        if (videoGrabber != null) {
            try {
                videoGrabber.adjustBrightness(brightness);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void setMinObjectHeight(double height) {
        if (height > 0.0) {
            minObjectHeight = height;
        }
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
            imagePainter.eraseAllPoints();
        }
    }

}
