package com.smoly87.fem.core.testutils;

import java.util.ArrayList;
import java.util.List;

public class NumericBlockMatrixBuilder {
    protected final List<List<Integer>> rows;
    protected int dimCount;
    protected NumericBlockMatrixBuilder() {
        rows = new ArrayList<>();
    }

    public static NumericBlockMatrixBuilder builder() {
        return new NumericBlockMatrixBuilder();
    }

    public NumericBlockMatrixBuilder setDimensionCount(int dimCount) {
        this.dimCount = dimCount;
        return this;
    }

    public NumericBlockMatrixBuilder addRowBlock(List<Integer> itemIndexes) {
        rows.add(itemIndexes);
        return this;
    }

    public double[][] build() {
        int colCount = rows.get(0).size();
        double[][] res = new double[rows.size() * dimCount][colCount * dimCount];
        for (int r = 0; r < rows.size(); r++) {
            List<Integer> rowItems = rows.get(r);
            for (int i = 0; i < dimCount; i++) {
                int rowInd = r * dimCount + i;
                for (int c = 0; c < colCount; c++) {
                    int elemValue = rowItems.get(c);
                    for (int j = 0; j < dimCount; j++) {
                        int colInd = c * dimCount + j;
                        res[rowInd][colInd] = elemValue;
                    }
                }
            }

        }
        return res;
    }
}
