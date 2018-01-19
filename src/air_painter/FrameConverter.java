package air_painter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.Contract;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by paul on 15/01/18.
 */
public class FrameConverter {

    @Contract("null -> null")
    public static Image convertToImage(Mat frame) {
        InputStream stream = convertToStream(frame);
        return getImageFromStream(stream);
    }

    @Contract("null -> null")
    public static InputStream convertToStream(Mat frame) {
        if (frame == null) {
            return null;
        }
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, buffer);
        return new ByteArrayInputStream(buffer.toArray());
    }

    @Contract("null -> null")
    private static Image getImageFromStream(InputStream stream) {
        if (stream == null) {
            return null;
        }
        try {
            BufferedImage awtImage = ImageIO.read(stream);
            return convertAWTImageToFXImage(awtImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Contract("null -> null")
    private static Image convertAWTImageToFXImage(BufferedImage image) {
        if (image == null) {
            return null;
        }
        return SwingFXUtils.toFXImage(image, null);
    }

}
