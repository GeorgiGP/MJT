package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.GrayscaleAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SobelEdgeDetectionTest {
    @Test
    void testSobelEdgeDetectionHorizontalLineUp() throws IOException {
        BufferedImage lineStart = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 3; i++) {
            lineStart.setRGB(i, 0, Color.WHITE.getRGB());
            lineStart.setRGB(i, 1, Color.BLACK.getRGB());
            lineStart.setRGB(i, 2, Color.BLACK.getRGB());
        }
        GrayscaleAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
        EdgeDetectionAlgorithm algorithm = new SobelEdgeDetection(grayscaleAlgorithm);
        BufferedImage result = algorithm.process(lineStart);
        WritableRaster raster = result.getRaster();
        File f = new File("test/ex.bmp");
        ImageIO.write(result, "bmp" , f);
        assertTrue(raster.getSample(1, 1, 0) > 0,
                "In 3x3 picture center should be defined when there is " +
                        "a vertical white line on 1st column and black other 2");
    }

    @Test
    void testSobelEdgeDetectionNull() {
        GrayscaleAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
        EdgeDetectionAlgorithm algorithm = new SobelEdgeDetection(grayscaleAlgorithm);
        assertThrows(IllegalArgumentException.class, () -> algorithm.process(null));
    }
}
