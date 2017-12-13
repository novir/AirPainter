package air_painter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    public boolean isCameraRunning() {
        return camera.isOpened();
    }

    public void initializeCamera(int cameraNumber) {
        camera = new VideoCapture(cameraNumber);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 600);
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 800);
        camera.set(Videoio.CV_CAP_PROP_BRIGHTNESS, 0.3);
    }

    public Mat getNextFrame() {
        Mat frame = new Mat();
        if(isCameraRunning()) {
            camera.read(frame);
        }
        return frame;
    }

    public BufferedImage getNextFrameAsImage() {
        Mat frame = getNextFrame();
        InputStream stream = convertFrameToStream(frame);
        return getImageFromStream(stream);
    }

    private InputStream convertFrameToStream(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".bmp", frame, buffer);
        return new ByteArrayInputStream(buffer.toArray());
    }

    private BufferedImage getImageFromStream(InputStream stream) {
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
