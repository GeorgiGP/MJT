package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class LuminosityGrayscale implements GrayscaleAlgorithm {
    private static final double LUMINOSITY_IDX_RED = 0.21;
    private static final double LUMINOSITY_IDX_GREEN = 0.71;
    private static final double LUMINOSITY_IDX_BLUE = 0.07;

    private int colorPixelInGrey(Color color) {
        return (int)(color.getRed() * LUMINOSITY_IDX_RED +
                color.getGreen() * LUMINOSITY_IDX_GREEN +
                color.getBlue() * LUMINOSITY_IDX_BLUE);
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }
        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage greyImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        WritableRaster raster = greyImage.getRaster();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                raster.setSample(x, y, 0, colorPixelInGrey(color));
            }
        }
        return greyImage;
    }
}
