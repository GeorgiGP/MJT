package bg.sofia.uni.fmi.mjt.imagekit.algorithm.kernel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class KernelTest {
    @Test
    void testKernelNull() {
        assertThrows(IllegalArgumentException.class, () -> new Kernel(null), "Kernel is null");
    }

    @Test
    void testKernelNullRow() {
        int[][] toInsert = new int[3][2];
        toInsert[1] = null;
        assertThrows(IllegalArgumentException.class, () -> new Kernel(toInsert), "Kernel row is null");
        toInsert[0] = null;
        assertThrows(IllegalArgumentException.class, () -> new Kernel(toInsert), "Kernel row is null");
    }

    @Test
    void testKernelRowsDifferentLength() {
        int[][] toInsert = new int[][] {
                {1, 2},
                {3, 4, 5},
        };
        assertThrows(IllegalArgumentException.class, () -> new Kernel(toInsert),
                "Kernel rows are with different length");
    }
}
