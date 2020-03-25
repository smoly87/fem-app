package com.smoly87.fem.core.blockmatrix;

import java.util.ArrayList;

public class SystemBlockRow {
    protected int rowSize;
    ArrayList<SystemBlockItems> values;

    public SystemBlockRow(int rowSize) {
        this.rowSize = rowSize;
        values = new ArrayList<>(rowSize);
    }

    public void addEntry(int colNum, SystemBlockItem systemBlockItem) {
        values.get(colNum).addItem(systemBlockItem);
    }

    public SystemBlockItems getItem(int colNum) {
        return values.get(colNum);
    }

}
