package air_painter;

import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pawel Pluta on 06/01/18.
 */
public class VideoController {

    private ScheduledExecutorService threadExecutor = null;

    private VideoGrabber videoGrabber = null;

    private VideoProcessor videoProcessor = null;

    private UIController uiController = null;

    private boolean requestedVideoOutput = false;

    public VideoController(@NotNull UIController controller) {
        videoGrabber = new VideoGrabber(0);
        videoProcessor = new VideoProcessor();
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
                Image fxImage = videoGrabber.getNextFrameAsImage();
                uiController.setImageToDisplay(fxImage);
            }
        };
        threadExecutor.scheduleAtFixedRate(videoCaptureInThread, initialDelay,
                                           replyPeriod, unit);
    }

    public void stopDisplay() {
        if (requestedVideoOutput) {
            requestedVideoOutput = false;
            shutdownThread(100, TimeUnit.MILLISECONDS);
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
            System.err.println("Thread executor interrupted while waiting");
            e.getStackTrace();
        }
    }

}
