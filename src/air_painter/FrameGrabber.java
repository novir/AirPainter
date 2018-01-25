package air_painter;

import org.jetbrains.annotations.NotNull;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.IOException;

/**
 * Created by Pawel Pluta on 12/12/17.
 */
public class FrameGrabber {

    private VideoCapture camera = null;

    public FrameGrabber(int deviceNumber) throws IOException {
        camera = new VideoCapture(deviceNumber);
        if (isCameraRunning()) {
            setDisplayWidth(640);
            setDisplayHeight(480);
            adjustBrightness(0.5);
        } else {
            throw new IOException("FrameGrabber: No camera found");
        }
    }

    public void openCamera(int deviceNumber) throws IOException {
        if (isCameraRunning()) {
            return;
        }
        camera.open(deviceNumber);
        if (!isCameraRunning()) {
            throw new IOException("FrameGrabber: No camera found");
        }
    }

    public void releaseCamera() throws IOException {
        if (isCameraRunning()) {
            camera.release();
        }
    }

    public boolean isCameraRunning() throws IOException {
        if (camera != null) {
            return camera.isOpened();
        } else {
            throw new IOException("FrameGrabber: No camera found");
        }
    }

    @NotNull
    public Mat getNextFrame() throws IOException {
        Mat frame = new Mat();
        if (isCameraRunning()) {
            camera.read(frame);
        } else {
            System.err.println("getNextFrame: Camera is not running");
        }
        return frame;
    }

    public void setCameraBrightness(double value) throws IOException {
        if (value >= 0.0 && value <= 1.0) {
            adjustBrightness(value);
        } else {
            System.err.println("setCameraBrightness: Brightness value out of range");
        }
    }

    private void adjustBrightness(double value) throws IOException {
        if (isCameraRunning()) {
            camera.set(Videoio.CV_CAP_PROP_BRIGHTNESS, value);
        } else {
            System.err.println("adjustBrightness: Camera is not running");
        }
    }

    public void setDisplayWidth(double width) throws IOException {
        if (isCameraRunning() && width > 0.0) {
            camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, width);
        } else {
            System.err.println("setDisplayWidth: Camera is not running");
        }
    }

    public void setDisplayHeight(double height) throws IOException {
        if (isCameraRunning() && height > 0.0) {
            camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, height);
        } else {
            System.err.println("setDisplayHeight: Camera is not running");
        }
    }

}
