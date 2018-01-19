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
public class ImagePainter {

    private Set<Point> drawingCoordinates = new HashSet<>();

    public void addNextPoint(@NotNull Point coordinates) {
        drawingCoordinates.add(coordinates);
    }

    public void drawAllPoints(@NotNull Mat frame) {
        for (Point point : drawingCoordinates) {
            Imgproc.circle(frame, point, 3,
                    new Scalar(0, 255, 0), 5);
        }
    }

    public void drawCircle(@NotNull Mat frame, @NotNull Point center) {
        Imgproc.circle(frame, center, 30,
                new Scalar(0, 0, 255), 4);
    }

    public void eraseAllPoints() {
        drawingCoordinates.clear();
    }

}
