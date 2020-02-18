package com.smoly87.rendering;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;

public class MatrixUtils {
    public static double[][] diagMatrix(double[] v) {
        final int N = v.length;
        double[][] A = new double[N][N];
        for (int i = 0; i < N; i++) {
             A[i][i] = v[i];
        }
        return A;
    }

    public static RealMatrix diagRealMatrix(double[] v) {
        return new Array2DRowRealMatrix(MatrixUtils.diagMatrix(v));
    }

    public static RealMatrix vecToRealMatrix(Vector2D v) {
        return new Array2DRowRealMatrix(v.toArray());
    }

    public static RealMatrix vecToRealMatrix(Vector3D v) {
        return new Array2DRowRealMatrix(v.toArray());
    }

    public static double[] concat(double[] first, double[] second) {
        double[] both = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, both, first.length, second.length);
        return both;
    }

    public static double[] getPartial(double[] arr, int startInd, int len) {
        double[] res = new double[len];
        System.arraycopy(arr, startInd, res, 0, len);
        return res;
    }

    public static Vector3D createFrom2D(Vector2D v) {
        return new Vector3D(concat(v.toArray(), new double[]{0}));
    }

    public static Vector2D createFrom3D(Vector3D v) {
        return new Vector2D(v.getX(), v.getY());
    }


    public static Vector3D createFrom2D(Vector2D v, double zValue) {
        return new Vector3D(concat(v.toArray(), new double[]{zValue}));
    }

    public static double crossProduct2d(Vector2D a, Vector2D b) {
        return createFrom2D(a).crossProduct(createFrom2D(b)).getZ();
    }


}
