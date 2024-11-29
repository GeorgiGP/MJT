package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.kernel.HorizontalSobelKernel;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.kernel.VerticalSobelKernel;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {
    private final ImageAlgorithm imageAlgorithm;
    private static final int COUNT_BYTE_VALUES = 255;

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        this.imageAlgorithm = grayscaleAlgorithm; //lazy initialization
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        BufferedImage greyScaled = imageAlgorithm.process(image);
        WritableRaster greyRaster = greyScaled.getRaster();
        BufferedImage result = new BufferedImage(greyScaled.getWidth(), greyScaled.getHeight(), greyScaled.getType());
        WritableRaster edgeRaster = result.getRaster();
        HorizontalSobelKernel hSobel = new HorizontalSobelKernel();
        VerticalSobelKernel vSobel = new VerticalSobelKernel();
        int height = hSobel.getKernel().getHeight();
        int width = hSobel.getKernel().getWidth();
        if (width != vSobel.getKernel().getWidth() || height != vSobel.getKernel().getHeight()) {
            throw new RuntimeException("Sobel kernels do not match");
        }
        for (int y = 1; y < result.getHeight() - 1; y++) {
            for (int x = 1; x < result.getWidth() - 1; x++) {
                int hChange = 0;
                int vChange = 0;
                for (int kerY = 0; kerY < height; kerY++) {
                    for (int kerX = 0; kerX < width; kerX++) {
                        int pixelGrey = greyRaster.getSample(x + kerX - 1, y + kerY - 1, 0);
                        hChange += (pixelGrey * hSobel.getKernel().getAt(kerY, kerX));
                        vChange += (pixelGrey * vSobel.getKernel().getAt(kerY, kerX));
                    }
                }
                int gradientMagnitude = (int)(Math.sqrt(hChange * hChange + vChange * vChange));
                gradientMagnitude = Math.min(gradientMagnitude, COUNT_BYTE_VALUES);
                edgeRaster.setSample(x, y, 0, gradientMagnitude);
            }
        }
        return result;
    }
}
