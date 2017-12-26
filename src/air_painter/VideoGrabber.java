package air_painter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
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

    public VideoGrabber(int cameraNumber) {
        camera = new VideoCapture(cameraNumber);
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 800);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 600);
        adjustBrightness(0.5);
    }

    public boolean isCameraRunning() {
        return camera.isOpened();
    }

    public Mat getNextFrame() {
        Mat frame = new Mat();
        if(isCameraRunning()) {
            camera.read(frame);
        } else {
            System.err.println("Camera is not running");
        }
        return frame;
    }

    @Nullable
    public BufferedImage getNextFrameAsImage() {
        Mat frame = getNextFrame();
        InputStream stream = convertFrameToStream(frame);
        return getImageFromStream(stream);
    }

    @Contract("null -> null")
    private InputStream convertFrameToStream(Mat frame) {
        if(frame == null) {
            return null;
        }
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".bmp", frame, buffer);
        return new ByteArrayInputStream(buffer.toArray());
    }

    @Contract("null -> null")
    private BufferedImage getImageFromStream(InputStream stream) {
        if(stream == null) {
            return null;
        }
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void adjustBrightness(double value) {
        if(value >= 0.0 && value <= 1.0) {
            camera.set(Videoio.CV_CAP_PROP_BRIGHTNESS, value);
        } else {
            System.err.println("Brightness value out of range");
        }
    }

}
