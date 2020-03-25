/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core.boundaryconditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Andrey
 */
public class BoundaryConditionsOld {
    protected double[] boundValues;
    protected double[] qValues;

    public List<Integer> getBoundIndexes() {
        return boundIndexes;
    }

    protected List<Integer> boundIndexes;
    protected List<Integer> boundIndexesAbs;

    public int getDimInd() {
        return dimInd;
    }

    // Number of the condition when have system of variables
    protected final int dimInd;

    public int getDimCount() {
        return dimCount;
    }

    protected final int dimCount;
    protected Map<Integer, Double> boundaryValuesMap;
    
    public double[] getBoundNodes() {
        return boundValues;
    }

    public List<Integer> getBoundIndexesAbs() {
        return boundIndexesAbs;
    }

    public BoundaryConditionsOld(double[] boundValues, ArrayList<Integer> boundIndexes, int dimInd, int dimCount) {
        this.boundValues = boundValues;
        this.boundIndexes = boundIndexes;
        this.boundIndexesAbs = boundIndexes.stream().map(ind -> ind * dimCount + dimInd).collect(Collectors.toList());
        boundaryValuesMap = new HashMap<>();
        for(int i = 0; i <  boundValues.length; i++) {
            boundaryValuesMap.put(boundIndexes.get(i), boundValues[i]);
        }
        this.dimInd = dimInd;
        this.dimCount = dimCount;
    }
    
    public BoundaryConditionsOld(double[] boundValues, ArrayList<Integer> boundIndexes) {
      this(boundValues, boundIndexes, 1, 1);
    }

    public BoundaryConditionsOld(double[] boundValues, Integer[] boundIndexes) {
        this(boundValues,  new ArrayList<>(Arrays.asList(boundIndexes)));        
    }
    
    public int  getPointIndex(int relInd){
        return boundIndexes.get(relInd);
    }

    public int getNodesCount(){
        return boundIndexes.size();
    }
    
    public double getBoundaryValue(int absInd){
        return boundaryValuesMap.get(absInd);
    }
}
