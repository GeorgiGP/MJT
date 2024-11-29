package bg.sofia.uni.fmi.mjt.imagekit.algorithm.kernel;

public class Kernel {
    private final int[][] kernel;

    public Kernel(int[][] kernel) {
        if (kernel == null) {
            throw new IllegalArgumentException("kernel is null");
        }
        this.kernel = kernel.clone();
        if (kernel[0] == null) {
            throw new IllegalArgumentException("kernels row is null");
        }
        int size = kernel[0].length;
        for (int i = 0; i < kernel.length; i++) {
            if (kernel[i] == null) {
                throw new IllegalArgumentException("kernels row is null");
            }
            if (size != kernel[i].length) {
                throw new IllegalArgumentException("kernels row does not have the same length");
            }
            this.kernel[i] = kernel[i].clone();
        }
    }

    public int getAt(int row, int col) {
        return kernel[row][col];
    }

    public int getHeight() {
        return kernel.length;
    }

    public int getWidth() {
        return kernel[0].length;
    }
}
