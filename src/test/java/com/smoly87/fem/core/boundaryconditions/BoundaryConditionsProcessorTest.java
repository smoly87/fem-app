package com.smoly87.fem.core.boundaryconditions;

import com.smoly87.fem.core.testutils.MatrixUtils;
import com.smoly87.fem.core.testutils.NumericBlockMatrixBuilder;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BoundaryConditionsProcessorTest {
    private static final int N = 5;
    private static final int DIM_COUNT = 2;

    private static final BoundaryConditions BOUNDARY_CONDITIONS_FIRST_AND_LAST =
            BoundaryConditions.builder(DIM_COUNT)
                    .setPointIndexes(new ArrayList(List.of(0, N - 1)))
                    .addValues(0, new double[]{1, 1})
                    .addValues(0, new double[]{2, 2})
                    .build();
    private static final double[][] MATRIX_5_5 =  NumericBlockMatrixBuilder
            .builder()
            .setDimensionCount(DIM_COUNT)
            .addRowBlock(List.of(11,12,13,14,15))
            .addRowBlock(List.of(21,22,23,24,25))
            .addRowBlock(List.of(31,32,33,34,35))
            .addRowBlock(List.of(41,42,43,44,45))
            .addRowBlock(List.of(51,52,53,54,55))
            .build();

    private BoundaryConditionsProcessor underTest;


    @Test
    public void applyBoundaryConditionsToLeftPart() {
        underTest = new BoundaryConditionsProcessor(BOUNDARY_CONDITIONS_FIRST_AND_LAST);
        final double[][] expectedRes = NumericBlockMatrixBuilder
                .builder()
                .setDimensionCount(DIM_COUNT)
                .addRowBlock(List.of(22, 23, 24))
                .addRowBlock(List.of(32, 33, 34))
                .addRowBlock(List.of(42, 43, 44))
                .build();

        double[][] res = underTest
                .applyBoundaryConditionsToLeftPart(new Array2DRowRealMatrix(MATRIX_5_5))
                .getData();
        MatrixUtils.assertArray2dEquals(expectedRes, res, 0.01);
    }


}




