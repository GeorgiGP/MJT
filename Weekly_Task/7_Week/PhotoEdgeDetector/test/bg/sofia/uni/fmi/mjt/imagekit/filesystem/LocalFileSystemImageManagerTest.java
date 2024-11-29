package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalFileSystemImageManagerTest {
    @Test
    void testLoadNullImg() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        assertThrows(IllegalArgumentException.class, () -> manager.loadImage(null), "Image is null");
    }

    @Test
    void testLoadNonExistentImg() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        assertThrows(IOException.class, () -> manager.loadImage(new File("jawjbfhhbawfbhjawhjawf")),
                "No validation if file does not exist");
    }

    @Test
    void testLoadFileWhichIsDirectory() {
        File dir = new File("test/testExamples");
        if (!dir.exists()) {
            dir.mkdir();
        }
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        File mockFile = mock();
        File testFile = new File("test/testExamples");

        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(testFile.isFile());

        assertThrows(IOException.class, () -> manager.loadImage(mockFile),
                "No validation if file is directory");
    }

    @Test
    void testLoadFileInvalidExtension() throws IOException {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        String fileName = "test/notOkayExtension.kabawf";
        File testFile = new File(fileName);

        assertDoesNotThrow(() -> new FileWriter(testFile));
        FileWriter writer = new FileWriter(testFile);
        writer.write(fileName);

        File mockFile = mock();
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.getName()).thenReturn(fileName);
        assertThrows(IOException.class, () -> manager.loadImage(testFile),
                "No validation if file has invalid extension");
    }

    @Test
    void testSaveImageNullFile() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        assertThrows(IllegalArgumentException.class,
                () -> manager.saveImage(new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB), null),
                "File is null");
    }

    @Test
    void testSaveImageNullImage() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        String testFileName = "test/testExamples.png";
        File testFile = new File(testFileName);
        assertThrows(IllegalArgumentException.class,
                () -> manager.saveImage(null, testFile),
                "Image is null");
    }

    @Test
    void testSaveImageNullBufferedImage() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        File testMock = mock();
        when(testMock.exists()).thenReturn(true);
        assertThrows(IOException.class,
                () -> manager.saveImage(new BufferedImage(2, 5, BufferedImage.TYPE_INT_RGB), testMock),
                "Can't save with name that exists");
    }

    @Test
    void testLoadDirectoryValid() throws IOException {
        BufferedImage toTest = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = toTest.getRaster();
        Color someColor = new Color(192, 241, 49);
        raster.setSample(0, 0, 0, someColor.getRed());
        raster.setSample(0, 0, 1, someColor.getGreen());
        raster.setSample(0, 0, 2, someColor.getBlue());

        BufferedImage toTest2 = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster2 = toTest2.getRaster();
        raster2.setSample(0, 0, 0, 120);
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        File dir = new File("test/testExamplesLoad");
        if (!dir.exists()) {
            dir.mkdir();
        }
        ImageIO.write(toTest, "png", new File(dir, "testExamples.png"));
        ImageIO.write(toTest2, "png", new File(dir, "testExamples2.png"));
        List<BufferedImage> extracted = manager.loadImagesFromDirectory(dir);
        assertEquals(extracted.getFirst().getRGB(0, 0), toTest.getRGB(0, 0), "Loading changed context");
        assertEquals(extracted.getLast().getRGB(0, 0), toTest2.getRGB(0, 0), "Loading changed context");
    }

    @Test
    void testSaveImageSuccess() throws IOException {
        BufferedImage toTest = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = toTest.getRaster();
        raster.setSample(0, 0, 0, 120);
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        File dir = new File("test/testExamplesSave");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File toSave = new File(dir, "testExampleSaved.png");
        if (toSave.exists()) {
            toSave.delete();
        }
        manager.saveImage(toTest, toSave);
        assertTrue(toSave.exists(), "Not saved when using saveImage");
        BufferedImage read = ImageIO.read(toSave);
        assertEquals(read.getRGB(0, 0), toTest.getRGB(0, 0), "Saving changed context");
    }
}
