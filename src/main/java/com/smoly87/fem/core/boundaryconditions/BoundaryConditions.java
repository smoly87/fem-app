package com.smoly87.fem.core.boundaryconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoundaryConditions {
    protected final int dimCount;
    protected final int boundaryNodesCount;
    protected final double[][] values;
    protected final ArrayList<Integer> boundIndexes;

    public int getDimCount() {
        return dimCount;
    }

    public int getBoundaryNodesCount() {
        return boundaryNodesCount;
    }

    public ArrayList<Integer> getBoundIndexes() {
        return boundIndexes;
    }

    protected BoundaryConditions(int dimCount,
                              int boundaryNodesCount,
                              double[][] values,
                              ArrayList<Integer> boundIndexes) {
        this.dimCount = dimCount;
        this.boundaryNodesCount = boundaryNodesCount;
        this.boundIndexes = boundIndexes;
        this.values = values;
    }

    public static BoundaryConditionsBuilder builder(int dimCount) {
        return new BoundaryConditionsBuilder(dimCount);
    }

    public double[] getBoundValues(int pointInd) {
       return values[pointInd];
    }

    /**
     *
     * @param pointInd Relative point index(number of a node)
     * @param variableInd Index of the variable(for systems values follow in format(u1,v1,u2,v2))
     * @return
     */
    public int getBoundIndexAbs(int pointInd, int variableInd) {
        int relPointPos = getBoundIndexes().get(pointInd);
        int absInd = relPointPos * dimCount + variableInd;
        return absInd;
    }

    public List<Integer> getBoundIndexAbs(int pointInd) {
        int relPointPos = getBoundIndexes().get(pointInd);
        int absInd = relPointPos * dimCount;
        List<Integer> res = new ArrayList<>(dimCount);
        for(int i = 0; i < dimCount; i++) {
            res.add(absInd + i) ;
        }
        return res;
    }

    public List<Integer> getBoundIndexesAbs() {
        int B =  getBoundaryNodesCount();
        List<Integer> res = new ArrayList<>(dimCount * B);
        for(int ind = 0; ind < B; ind++) {
          List<Integer> curIndValues = getBoundIndexAbs(ind);
          res.addAll(curIndValues);
        }
        return res;
    }
}
