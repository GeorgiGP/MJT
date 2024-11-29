package bg.sofia.uni.fmi.mjt.imagekit.algorithm.kernel;

public class HorizontalSobelKernel {
    private static final Kernel KERNEL;

    static {
        KERNEL = new Kernel(new int[][]{
                {-1, -1 - 1, -1},
                {0, 0, 0},
                {1, 1 + 1, 1}
        });
    }

    public Kernel getKernel() {
        return KERNEL;
    }
}
