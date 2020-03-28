package com.smoly87.fem.core.blockmatrix;

import com.smoly87.fem.core.ElemFuncType;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SystemBlockMatrixTest {

    @Test
    public void calculateOnElement() {
    }

    @Test
    public void multiplyTwoSymbolicMatrixes() {
        SystemBlockMatrix A   = new SystemBlockMatrix(new ElemFuncType[][]{
                {ElemFuncType.dFdx, ElemFuncType.dFdx},
                {ElemFuncType.dFdx, ElemFuncType.dFdx},
        }, new double[][]{
                {2, 2},
                {2, 2},
        });
        SystemBlockMatrix B   = new SystemBlockMatrix(new ElemFuncType[][]{
                {ElemFuncType.dFdy, ElemFuncType.dFdy},
                {ElemFuncType.dFdy, ElemFuncType.dFdy},
        }, new double[][]{
                {3, 3},
                {3, 3},
        });

        SystemBlockMatrix C = A.multiply(B);
        assertElementHasProductionOfXy(C, 0, 0);
        assertElementHasProductionOfXy(C, 0, 1);
        assertElementHasProductionOfXy(C, 1, 0);
        assertElementHasProductionOfXy(C, 1, 1);
    }

    protected void assertElementHasProductionOfXy(SystemBlockMatrix C, int r, int c) {
        List<SystemBlockItem> systemBlockItemsList = C.getEntry(0, 0).getItems();
        long cnt = systemBlockItemsList.stream()
                .filter(systemBlockItem -> systemBlockItem.getElemFuncType1().equals(ElemFuncType.dFdx))
                .filter(systemBlockItem -> systemBlockItem.getElemFuncType2().equals(ElemFuncType.dFdy))
                .filter(systemBlockItem -> Math.abs(systemBlockItem.getMultiplicator() - 6) < 0.001)
                .count();
        assertEquals(2, cnt);
    }

    @Test
    public void multiplySymbolicElems() {
    }
}