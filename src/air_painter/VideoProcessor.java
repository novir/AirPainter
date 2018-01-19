package air_painter;

import org.jetbrains.annotations.NotNull;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pawel Pluta on 12/12/17.
 */
public class VideoProcessor {

    public static Mat findBlueObject(@NotNull Mat originalFrame) {
        Mat frame = VideoProcessor.convertBGRToHSB(originalFrame);
        frame = VideoProcessor.performBlueObjectThreshold(frame);
        frame = VideoProcessor.subtractBackground(frame);
        frame = VideoProcessor.performAdaptiveThreshold(frame);
        frame = VideoProcessor.applyMorphologicalOpening(frame, 5);
        frame = VideoProcessor.applyMorphologicalClosing(frame, 5);
        frame = VideoProcessor.applyDilation(frame, 10);
        return frame;
    }

    public static Mat convertBGRToHSB(@NotNull Mat bgrFrame) {
        Mat hsbFrame = new Mat();
        Imgproc.cvtColor(bgrFrame, hsbFrame, Imgproc.COLOR_BGR2HSV);
        return hsbFrame;
    }

    public static Mat convertBGRToGray(@NotNull Mat bgrFrame) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(bgrFrame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        return grayFrame;
    }

    public static Mat performBlueObjectThreshold(@NotNull Mat frame) {
        Mat filteredFrame = new Mat();
        Scalar minHSB = new Scalar(105, 100, 100);
        Scalar maxHSB = new Scalar(135, 255, 255);
        Core.inRange(frame, minHSB, maxHSB, filteredFrame);
        return filteredFrame;
    }

    public static Mat applyMorphologicalOpening(@NotNull Mat frame, double k) {
        Mat filteredFrame = new Mat();
        Mat structuringElement =
                Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(k, k));
        Imgproc.erode(frame, filteredFrame, structuringElement);
        Imgproc.dilate(filteredFrame, filteredFrame, structuringElement);
        return filteredFrame;
    }

    public static Mat applyMorphologicalClosing(@NotNull Mat frame, double k) {
        Mat filteredFrame = new Mat();
        Mat structuringElement =
                Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(k, k));
        Imgproc.dilate(frame, filteredFrame, structuringElement);
        Imgproc.erode(filteredFrame, filteredFrame, structuringElement);
        return filteredFrame;
    }

    public static Mat applyDilation(@NotNull Mat frame, double k) {
        Mat filteredFrame = new Mat();
        Mat structuringElement =
                Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(k, k));
        Imgproc.dilate(frame, filteredFrame, structuringElement);
        return filteredFrame;
    }

    public static Mat performAdaptiveThreshold(@NotNull Mat frame) {
        Mat filteredFrame = new Mat();
        Imgproc.adaptiveThreshold(frame, filteredFrame, 255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV,
                11, 2);
        return filteredFrame;
    }

    public static Mat subtractBackground(@NotNull Mat frame) {
        Mat foregroundMask = new Mat();
        BackgroundSubtractorMOG2 bgSubtractor =
                Video.createBackgroundSubtractorMOG2();
        bgSubtractor.apply(frame, foregroundMask);
        return foregroundMask;
    }

    @NotNull
    public static List<MatOfPoint> findBigContours(@NotNull Mat trackedObject,
                                                   double minContourHeight) {
        List<MatOfPoint> contours = findContours(trackedObject);
        contours = removeSmallContours(contours, minContourHeight);
        return contours;
    }

    @NotNull
    public static List<MatOfPoint> findContours(@NotNull Mat trackedObject) {
        List<MatOfPoint> contours = new ArrayList<>(20);
        Imgproc.findContours(trackedObject, contours, new Mat(), Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        return contours;
    }

    @NotNull
    public static List<MatOfPoint> removeSmallContours(@NotNull List<MatOfPoint> contours,
                                                       double minContourHeight) {
        List<MatOfPoint> bigContours = new ArrayList<>(10);
        for (MatOfPoint contour : contours) {
            if (contour.height() > minContourHeight) {
                bigContours.add(contour);
            }
        }
        return bigContours;
    }

    @NotNull
    public static Point calculateAvgCentroid(@NotNull List<MatOfPoint> contours) {
        double avgX = 0.0;
        double avgY = 0.0;
        double contoursCount = 0;
        for (MatOfPoint contour : contours) {
            Point centroid = calculateCentroid(contour);
            avgX += centroid.x;
            avgY += centroid.y;
            contoursCount++;
        }
        avgX /= contoursCount;
        avgY /= contoursCount;
        return new Point(avgX, avgY);
    }

    @NotNull
    public static Point calculateCentroid(@NotNull MatOfPoint contour) {
        Moments moments = Imgproc.moments(contour);
        double cX = moments.get_m10() / moments.get_m00();
        double cY = moments.get_m01() / moments.get_m00();
        return new Point(cX, cY);
    }

}
