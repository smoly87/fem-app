package com.smoly87.fem.core.boundaryconditions;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class BoundaryConditionsBuilder {
    protected ArrayList<Integer> pointIndexes;
    protected final double[][]values;
    protected final int dimCount;

    public BoundaryConditionsBuilder(int dimCount) {
        this.dimCount = dimCount;
        values = new double[dimCount][];
    }

    public BoundaryConditionsBuilder setPointIndexes(List<Integer> pointIndexes) {
        this.pointIndexes = new ArrayList<>(pointIndexes);
        return this;
    }

    public BoundaryConditionsBuilder addValues(int dimInd, double[] values) {
        checkArgument(dimCount > 0, "Please specify dimensions count first");
        checkArgument(dimInd < dimCount, "Dimensions index  exceeds range");
        this.values[dimInd] = values;
        return this;
    }

    public BoundaryConditionsBuilder addValues( double[] values) {
        return addValues(0, values);
    }

    public BoundaryConditions build() {
        checkArgument(dimCount > 0, "Please specify dimensions count");
        checkArgument(pointIndexes.size() > 0, "Please specify at least one point index.");
        return new BoundaryConditions(dimCount, values[0].length, values, pointIndexes);
    }
}
