package bg.sofia.uni.fmi.mjt.imagekit.algorithm.kernel;

public class VerticalSobelKernel {
    private static final Kernel KERNEL;

    static {
        KERNEL = new Kernel(new int[][]{
                {-1, 0, 1},
                {-1 - 1, 0, 2},
                {-1, 0, 1}
        });
    }

    public Kernel getKernel() {
        return KERNEL;
    }
}
