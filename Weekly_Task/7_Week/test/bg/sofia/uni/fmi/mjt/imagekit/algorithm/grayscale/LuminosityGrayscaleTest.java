package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.EdgeDetectionAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.SobelEdgeDetection;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LuminosityGrayscaleTest {
    private int colorPixelInGrey(Color color) {
        return (int)(color.getRed() * 0.21 +
                color.getGreen() * 0.71 +
                color.getBlue() * 0.07);
    }

    @Test
    void testLuminosityGrayscaleSmallImg() {
        GrayscaleAlgorithm algorithm = new LuminosityGrayscale();
        BufferedImage toTest = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = toTest.getRaster();
        Color someColor = new Color(192, 241, 49);
        int colorPixelInGrey = colorPixelInGrey(someColor);
        raster.setSample(0, 0, 0, someColor.getRed());
        raster.setSample(0, 0, 1, someColor.getGreen());
        raster.setSample(0, 0, 2, someColor.getBlue());
        BufferedImage toTest2 = algorithm.process(toTest);
        WritableRaster raster2 = toTest2.getRaster();

        assertEquals(colorPixelInGrey, raster2.getSample(0, 0, 0), "Wrong color transformation");
        assertThrows(ArrayIndexOutOfBoundsException.class,() -> raster2.getSample(0, 0, 1), "Does not have green value because its gray");
        assertThrows(ArrayIndexOutOfBoundsException.class,() -> raster2.getSample(0, 0, 2), "Does not have blue value because its gray");
    }

    @Test
    void testLuminosityGrayscaleNull() {
        GrayscaleAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
        assertThrows(IllegalArgumentException.class, () -> grayscaleAlgorithm.process(null));
    }
}
