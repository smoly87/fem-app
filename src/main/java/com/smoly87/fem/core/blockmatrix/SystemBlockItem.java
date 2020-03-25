package com.smoly87.fem.core.blockmatrix;

import com.smoly87.fem.core.ElemFuncType;

public class SystemBlockItem {
    public ElemFuncType getElemFuncType1() {
        return elemFuncType1;
    }

    public ElemFuncType getElemFuncType2() {
        return elemFuncType2;
    }

    public double getMultiplicator() {
        return multiplicator;
    }

    protected ElemFuncType elemFuncType1;
    protected ElemFuncType elemFuncType2;
    protected double multiplicator;

    public SystemBlockItem(ElemFuncType elemFuncType1, ElemFuncType elemFuncType2, double multiplicator) {
        this.elemFuncType1 = elemFuncType1;
        this.elemFuncType2 = elemFuncType2;
        this.multiplicator = multiplicator;
    }
}
