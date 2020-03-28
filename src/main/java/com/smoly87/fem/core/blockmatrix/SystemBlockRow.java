package com.smoly87.fem.core.blockmatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SystemBlockRow {
    protected int rowSize;
    Map<Integer, SystemBlockItems> values;

    public SystemBlockRow(int rowSize) {
        this.rowSize = rowSize;
        values = new HashMap<>();
    }

    public void addEntry(int colNum, SystemBlockItem systemBlockItem) {
        values.computeIfAbsent(colNum, (v) -> new SystemBlockItems() );

        values.get(colNum).addItem(systemBlockItem);
    }

    public SystemBlockItems getItem(int colNum) {
        return values.get(colNum);
    }

}
