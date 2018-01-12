package air_painter;

import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Mat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pawel Pluta on 06/01/18.
 */
public class VideoController {

    private ScheduledExecutorService threadExecutor = null;

    private VideoGrabber videoGrabber = null;

    private UIController uiController = null;

    private double minObjectHeight = 25.0;

    private boolean requestedVideoOutput = false;

    public VideoController(@NotNull UIController controller) {
        videoGrabber = new VideoGrabber(0);
        this.uiController = controller;
    }

    public void startDisplay() {
        requestedVideoOutput = true;
        videoGrabber.openCamera(0);
        startDisplayThread(0, 10, TimeUnit.MILLISECONDS);
    }

    private void startDisplayThread(long initialDelay, long replyPeriod,
                                    TimeUnit unit) {
        threadExecutor = Executors.newSingleThreadScheduledExecutor();
        Runnable videoCaptureInThread = () -> {
            if (requestedVideoOutput) {
                Image fxImage = getProcessedFrameAsImage();
                uiController.setImageToDisplay(fxImage);
            }
        };
        threadExecutor.scheduleAtFixedRate(videoCaptureInThread, initialDelay,
                                           replyPeriod, unit);
    }

    private Image getProcessedFrameAsImage() {
        Mat frame = new Mat();
        if(videoGrabber.isCameraRunning()) {
            frame = videoGrabber.getNextFrame();
            frame = VideoProcessor.trackBlueObject(frame, minObjectHeight);
        }
        return videoGrabber.convertFrameToImage(frame);
    }

    public void stopDisplay() {
        if (requestedVideoOutput) {
            requestedVideoOutput = false;
            stopDisplayThread(100, TimeUnit.MILLISECONDS);
            videoGrabber.releaseCamera();
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

    public void setCameraBrightness(double brightness) {
        if (brightness >= 0.0 && brightness <= 1.0) {
            videoGrabber.adjustBrightness(brightness);
        } else {
            System.err.println("VideoController: " +
                               "Camera brightness out of range");
        }
    }

    public void setMinObjectHeight(double height) {
        if (height >= 0.0) {
            minObjectHeight = height;
        }
    }

}
