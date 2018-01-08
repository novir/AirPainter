package air_painter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Pawel Pluta on 12/12/17.
 */
public class VideoProcessor {

    public static Mat convertBGRToHSB(Mat bgrFrame) {
        Mat hsbFrame = new Mat();
        if (bgrFrame != null) {
            Imgproc.cvtColor(bgrFrame, hsbFrame, Imgproc.COLOR_BGR2HSV);
        } else {
            System.err.println("Color conversion: Frame can't be null");
        }
        return hsbFrame;
    }

    public static Mat convertBGRToGray(Mat bgrFrame) {
        Mat grayFrame = new Mat();
        if (bgrFrame != null) {
            Imgproc.cvtColor(bgrFrame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        } else {
            System.err.println("Color conversion: Frame can't be null");
        }
        return grayFrame;
    }

    public static Mat performThresholding(Mat frame, int brightness) {
        Mat filteredFrame = new Mat();
        Scalar minHSB = new Scalar(110, 50, 50);
        Scalar maxHSB = new Scalar(130, 255, brightness);
        if (frame != null) {
            Core.inRange(frame, minHSB, maxHSB, filteredFrame);
        } else {
            System.err.println("Color thresholding: Frame can't be null");
        }
        return filteredFrame;
    }

    public static Mat applyMorphologicalOpening(Mat frame, int k) {
        Mat filteredFrame = new Mat();
        Mat structuringElement =
                Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(k, k));
        if (frame != null) {
            Imgproc.erode(frame, filteredFrame, structuringElement);
            Imgproc.dilate(filteredFrame, filteredFrame, structuringElement);
        } else {
            System.err.println("Morphological opening: Frame can't be null");
        }
        return filteredFrame;
    }

    public static Mat applyMorphologicalClosing(Mat frame, int k) {
        Mat filteredFrame = new Mat();
        Mat structuringElement =
                Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(k, k));
        if (frame != null) {
            Imgproc.dilate(frame, filteredFrame, structuringElement);
            Imgproc.erode(filteredFrame, filteredFrame, structuringElement);
        } else {
            System.err.println("Morphological closing: Frame can't be null");
        }
        return filteredFrame;
    }

}
