package air_painter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.Color;

/**
 * Created by Pawel Pluta on 12/12/17.
 */
public class VideoProcessor {



    public Mat performThresholding(Mat frame, Color minRGBValue, Color maxRGBValue) {
        Mat filteredFrame = new Mat();
        Scalar minValues =
                new Scalar(minRGBValue.getBlue(), minRGBValue.getGreen(), minRGBValue.getRed());
        Scalar maxValues =
                new Scalar(maxRGBValue.getBlue(), maxRGBValue.getGreen(), maxRGBValue.getRed());
        if (frame != null) {
            Core.inRange(frame, minValues, maxValues, filteredFrame);
        } else {
            System.err.println("Color thresholding: Frame can't be null");
        }
        return filteredFrame;
    }

    public Mat applyMorphologicalOpening(Mat frame, int k) {
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

    public Mat applyMorphologicalClosing(Mat frame, int k) {
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
