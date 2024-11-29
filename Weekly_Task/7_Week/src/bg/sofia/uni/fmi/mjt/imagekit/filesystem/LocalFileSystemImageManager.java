package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LocalFileSystemImageManager implements FileSystemImageManager {
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("png", "bmp", "jpeg", "jpg");

    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("Image file is null");
        }
        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IOException("Invalid image file format");
        }
        for (String extension : SUPPORTED_EXTENSIONS) {
            if (imageFile.getName().toLowerCase().endsWith(extension)) {
                return ImageIO.read(imageFile);
            }
        }
        throw new IOException("Not from the supported image format");
    }

    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Directory is null");
        }
        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            throw new IOException("Invalid directory");
        }
        List<BufferedImage> result = new ArrayList<>();
        for (File file : Objects.requireNonNull(imagesDirectory.listFiles())) {
            result.add(loadImage(file));
        }
        return result;
    }

    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {
        if (image == null || imageFile == null) {
            throw new IllegalArgumentException("Image or image file is null");
        }
        if (imageFile.exists()) {
            throw new IOException("Image with name " + imageFile + " already exists");
        }
        if (imageFile.getParentFile() != null && !imageFile.getParentFile().exists()) {
            throw new IOException("Image directory parent " + imageFile.getParent() + " does not exist");
        }
        for (String extension : SUPPORTED_EXTENSIONS) {
            if (imageFile.getName().toLowerCase().endsWith(extension)) {
                ImageIO.write(image, extension, imageFile);
                return;
            }
        }
        throw new IOException("File is not from the supported image format");
    }
}
