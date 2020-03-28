package com.smoly87.fem.core.testutils;

import static org.junit.Assert.assertArrayEquals;

public class MatrixUtils {
    public static void assertArray2dEquals(double[][] expected, double[][] res, double delta) {
        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i], res[i], delta);
        }
    }
}
