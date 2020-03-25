package com.smoly87.fem.core.boundaryconditions;

import java.util.ArrayList;
import java.util.List;

public class BoundaryConditionsValue {
    protected int dimCount;
    // Index in scope, for example for systems the follow like (u1, v1, )
    protected int variableIndex;
    protected ArrayList<Integer> pointIndexes;
    protected List<Double> values;

    public BoundaryConditionsValue(int dimCount, int variableIndex, ArrayList<Integer> pointIndexes, List<Double> values) {
        this.dimCount = dimCount;
        this.variableIndex = variableIndex;
        this.pointIndexes = pointIndexes;
        this.values = values;
    }
}


