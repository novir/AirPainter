package air_painter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * Created by Pawel Pluta on 12/12/17.
 */
public class VideoGrabber {

    private VideoCapture camera = null;
    private Mat frame = new Mat();

    public void loadOpenCVLibrary() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public void initializeCamera(int cameraNumber) {
        camera = new VideoCapture(cameraNumber);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 600);
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 800);
        camera.set(Videoio.CV_CAP_PROP_BRIGHTNESS, 0.3);
    }

    public boolean isCameraRunning() {
        return camera.isOpened();
    }

    public Mat getNextFrame() {
        camera.read(frame);
        return frame;
    }

}
