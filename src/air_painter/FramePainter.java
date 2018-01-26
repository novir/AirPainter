package air_painter;

import org.jetbrains.annotations.NotNull;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by paul on 12/01/18.
 */
public class FramePainter {

    private Map<Integer, Point> drawingCoordinates = new ConcurrentHashMap<>();

    public void addNextPoint(@NotNull Point coordinates) {
        drawingCoordinates.put(coordinates.hashCode(), coordinates.clone());
    }

    public Mat drawAllPoints(@NotNull Mat frame) {
        Mat result = frame.clone();
        for (Point point : drawingCoordinates.values()) {
            Imgproc.circle(result, point, 10,
                    new Scalar(0, 255, 0), 20);
        }
        return result;
    }

    public Mat drawCircle(@NotNull Mat frame, @NotNull Point center) {
        Mat result = frame.clone();
        Imgproc.circle(result, center, 30,
                new Scalar(0, 0, 255), 4);
        return result;
    }

    public void eraseAllPoints() {
        drawingCoordinates.clear();
    }

}
