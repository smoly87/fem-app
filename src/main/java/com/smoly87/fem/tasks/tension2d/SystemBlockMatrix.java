package com.smoly87.fem.tasks.tension2d;

import com.smoly87.fem.core.ElemFuncType;
import com.smoly87.fem.core.Element;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
public class SystemBlockMatrix {
    protected ArrayList<SystemBlockRow> rows;
    protected int rowCount;
    protected int colCount;

    public SystemBlockMatrix(int rowCount, int colCount) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        rows = new ArrayList<>(rowCount);
    }

    public SystemBlockMatrix(ElemFuncType[][] values) {
        for(int i = 0; i < values.length; i++ ) {
            for (int j = 0; j < values[i].length; j++) {
                addValueToEntry(i, j,  new SystemBlockItem(values[i][j], ElemFuncType.I, 1.0d) );
            }
        }
    }

    public void addValueToEntry(int rowNum, int colNum, SystemBlockItem systemBlockItem) {
        rows.get(rowNum).addEntry(colNum, systemBlockItem);
    }

    public SystemBlockItems getEntry(int rowCount, int colCount) {
        return rows.get(rowCount).getItem(colCount);
    }

    public RealMatrix calculateOnElement(Element elem, Integer l, Integer m) {
        RealMatrix result = new Array2DRowRealMatrix(rowCount, colCount);
       for(int r = 0; r < getRowCount(); r++) {
            for(int c = 0; c < getColCount(); c++) {
                SystemBlockItems blockItems  = rows.get(r).getItem(c);
                double value = calculate(blockItems, elem,  l, m);
                result.setEntry(r, c, value);
            }
        }
       return result;
    }

    private double calculate( SystemBlockItems blockItems, Element elem, Integer l, Integer m) {
        double s = 0;
        for(int i = 0; i < blockItems.size(); i++) {
            s += elem.getElemFunc().integrate(
                    blockItems.getItem(i).getElemFuncType1(),
                    blockItems.getItem(i).getElemFuncType1(), l, m);
        }
        return s;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    public SystemBlockMatrix multiply(SystemBlockMatrix B) {
        SystemBlockMatrix result = new SystemBlockMatrix(this.rowCount, B.colCount);
        for (int r = 0; r < this.getRowCount(); r++) {
            for (int c = 0; c < B.getColCount(); c++) {
                for (int k = 0; k < this.getColCount(); k++) {
                    int finalR = r;
                    int finalK = k;
                    multiplySymbolicElems(
                            this.getEntry(r, k),
                            B.getEntry(k, c),
                            result, r, c);
                }
            }
        }
        return result;
    }

    protected void multiplySymbolicElems(SystemBlockItems A, SystemBlockItems B,
                                         SystemBlockMatrix result, int destRow, int destCol ) {
        for (int i = 0; i < A.size(); i++) {
            for (int j = 0; j < B.size(); j++) {
                result.addValueToEntry(
                        destRow,
                        destCol,
                        new SystemBlockItem(
                                A.getItem(i).getElemFuncType1(),
                                B.getItem(j).getElemFuncType1(),
                                A.getItem(i).getMultiplicator() * B.getItem(j).getMultiplicator()
                        )
                );
            }
        }
    }

    private SystemBlockMatrix convertToSymbolic(RealMatrix B) {
        SystemBlockMatrix result = new SystemBlockMatrix(B.getRowDimension(), B.getColumnDimension());
        for (int r = 0; r < B.getRowDimension(); r++) {
            for (int c = 0; c < B.getColumnDimension(); c++) {
                result.addValueToEntry(r, c, new SystemBlockItem(ElemFuncType.I, ElemFuncType.I, B.getEntry(r, c)));
            }
        }
        return result;
    }
}
