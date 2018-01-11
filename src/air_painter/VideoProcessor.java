package air_painter;

import org.jetbrains.annotations.NotNull;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pawel Pluta on 12/12/17.
 */
public class VideoProcessor {

    public static Mat trackBlueObject(@NotNull Mat rawFrame) {
        Mat frame = VideoProcessor.convertBGRToHSB(rawFrame);
        frame = VideoProcessor.performBlueThreshold(frame);
        frame = VideoProcessor.subtractBackground(frame);
        frame = VideoProcessor.performAdaptiveThreshold(frame);
        frame = VideoProcessor.applyMorphologicalOpening(frame, 5);
        frame = VideoProcessor.applyMorphologicalClosing(frame, 5);
        frame = VideoProcessor.applyDilation(frame, 10);
        drawContours(rawFrame, frame, 50);
        return rawFrame;
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

    public static Mat performBlueThreshold(@NotNull Mat frame) {
        Mat filteredFrame = new Mat();
        Scalar minHSB = new Scalar(110, 100, 100);
        Scalar maxHSB = new Scalar(130, 255, 255);
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

    public static void drawContours(@NotNull Mat rawFrame,
                                    @NotNull Mat processedFrame,
                                    double contourHeight) {
        List<MatOfPoint> contours = findContours(processedFrame);
        for(int i = 0; i < contours.size(); i++) {
            if(contours.get(i).size().height > contourHeight) {
                Imgproc.drawContours(rawFrame, contours, i,
                        new Scalar(0,0,255), 4);
            }
        }
    }

    public static List<MatOfPoint> findContours(@NotNull Mat frame) {
        List<MatOfPoint> contours = new ArrayList<>(20);
        Imgproc.findContours(frame, contours, new Mat(), Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_NONE, new Point(0,0));
        return contours;
    }

}
