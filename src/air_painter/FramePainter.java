package air_painter;

import org.jetbrains.annotations.NotNull;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by paul on 12/01/18.
 */
public class FramePainter {

    private Set<Point> drawingCoordinates = new HashSet<>();

    public void addNextPoint(@NotNull Point coordinates) {
        drawingCoordinates.add(coordinates);
    }

    public Mat drawAllPoints(@NotNull Mat frame) {
        Mat result = frame.clone();
        for (Point point : drawingCoordinates) {
            Imgproc.circle(result, point, 3,
                    new Scalar(0, 255, 0), 5);
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
