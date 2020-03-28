package com.smoly87.fem.core.blockmatrix;

import com.smoly87.fem.core.Element;
import com.smoly87.fem.core.Mesh;
import com.smoly87.fem.core.SimpleMeshBuilder;
import com.smoly87.fem.core.testutils.MatrixUtils;
import com.smoly87.fem.core.testutils.NumericBlockMatrixBuilder;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BlockMatrixStiffnessMatrixBuilderTest {
    private static final int DIM_COUNT = 2;
    private BlockMatrixStiffnessMatrixBuilder underTest;

    @Mock
    SystemBlockMatrix systemBlockMatrix;

    @Test
    public void fillGlobalStiffness() {
        final int blockSize = 2;
        Mockito.when(systemBlockMatrix.calculateOnElement(Matchers.any(), Matchers.any(), Matchers.any()))
                .thenAnswer(invocation -> {
                    Element elem = (Element)invocation.getArguments()[0];
                    List<Integer> numsList = elem.getNodesList();
                    int l = (int) invocation.getArguments()[1];
                    int m = (int) invocation.getArguments()[2];
                    int i = numsList.get(l) ;
                    int j = numsList.get(m) ;

                    double[][] data = createUniformElemMatrix(blockSize, (i + 1) * 10 + (j + 1));
                    return new Array2DRowRealMatrix(data);
                });
        Mockito.when(systemBlockMatrix.getRowCount()).thenReturn(2);
        Mesh mesh = SimpleMeshBuilder.create1dLineMesh(2, 1, true);
        final double[][] expectedAnswer = NumericBlockMatrixBuilder
                .builder()
                .setDimensionCount(DIM_COUNT)
                .addRowBlock(List.of(11, 12, 0))
                .addRowBlock(List.of(21, 44, 23))
                .addRowBlock(List.of(0, 32, 33))
                .build();

        RealMatrix res = underTest.fillGlobalStiffness(mesh, systemBlockMatrix);

        MatrixUtils.assertArray2dEquals(expectedAnswer, res.getData(), 0.001);
    }

    public double[][] createUniformElemMatrix( int blockSize, double value) {
        double[][] res = new double[blockSize][blockSize];
        for(int i = 0; i < blockSize; i++) {
            for(int j = 0; j < blockSize; j++) {
                res[i][j] = value;
            }
        }
        return res;
    }
}