package com.smoly87.rendering;

public class MathUtils {
    protected static double deltaAllowed = 1e-15;
    public static boolean isCloseTo(double value, double target, double delta) {
        return Math.abs(value - target) < delta;
    }

    public static boolean isCloseTo(double value, double target) {
        return Math.abs(value - target) < deltaAllowed;
    }

}
