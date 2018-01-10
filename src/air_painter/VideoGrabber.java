package air_painter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pawel Pluta on 12/12/17.
 */
public class VideoGrabber {

    private VideoCapture camera = null;

    public static void loadOpenCVLibrary() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public VideoGrabber(int deviceNumber) {
        loadOpenCVLibrary();
        camera = new VideoCapture(deviceNumber);
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 640);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 480);
        adjustBrightness(0.5);
    }

    public boolean isCameraRunning() {
        return camera.isOpened();
    }

    @NotNull
    public Mat getNextFrame() {
        Mat frame = new Mat();
        if (isCameraRunning()) {
            camera.read(frame);
        } else {
            System.err.println("VideoGrabber: Camera is not running");
        }
        return frame;
    }

    @Contract("null -> null")
    public Image convertFrameToImage(Mat frame) {
        InputStream stream = convertFrameToStream(frame);
        return getImageFromStream(stream);
    }

    @NotNull
    public Image getNextFrameAsImage() {
        Mat frame = getNextFrame();
        InputStream stream = convertFrameToStream(frame);
        return getImageFromStream(stream);
    }

    @Contract("null -> null")
    private InputStream convertFrameToStream(Mat frame) {
        if (frame == null) {
            return null;
        }
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, buffer);
        return new ByteArrayInputStream(buffer.toArray());
    }

    @Contract("null -> null")
    private Image getImageFromStream(InputStream stream) {
        if (stream == null) {
            return null;
        }
        try {
            BufferedImage awtImage = ImageIO.read(stream);
            return convertAWTImageToFXImage(awtImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Contract("null -> null")
    private Image convertAWTImageToFXImage(BufferedImage image) {
        if (image == null) {
            return null;
        }
        return SwingFXUtils.toFXImage(image, null);
    }

    public void adjustBrightness(double value) {
        if (value >= 0.0 && value <= 1.0) {
            camera.set(Videoio.CV_CAP_PROP_BRIGHTNESS, value);
        } else {
            System.err.println("VideoGrabber: Brightness value out of range");
        }
    }

    public void openCamera(int deviceNumber) {
        if (!isCameraRunning()) {
            camera.open(deviceNumber);
        }
    }

    public void releaseCamera() {
        if (isCameraRunning()) {
            camera.release();
        }
    }

}
