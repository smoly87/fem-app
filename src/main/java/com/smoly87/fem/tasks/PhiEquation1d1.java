/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.tasks;

import com.smoly87.fem.core.boundaryconditions.BoundaryConditions;
import com.smoly87.fem.core.boundaryconditions.BoundaryConditionsOld;
import com.smoly87.fem.core.*;
import org.apache.commons.math3.linear.*;

import java.util.List;

/**
 *
 * @author Andrey
 */
// This task is from page 108 Zenkevic
// Try to eliminate rows with boundary conditions end estimate accuracy.
public class PhiEquation1d1 extends Task {
    private final int elemNum;


    public PhiEquation1d1( int elemNum) {
        this.elemNum = elemNum;
    }

    protected double Klm(Element elem, Integer l, Integer m){
        //return elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.d2Fdx, l, m)-elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.F, l, m);
        //Weak form
        return elem.getElemFunc().integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, l, m)+elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.F, l, m);
    }
    
    protected void init() {
        mesh = SimpleMeshBuilder.create1dLineMesh(elemNum,1, true);
         
        this.initMatrixes(mesh.getNodesCount());
        
        K = fillGlobalStiffness(K, this::Klm);
        


        boundaryConditions = BoundaryConditions.builder(1)
                .setPointIndexes(List.of(0, mesh.getNodesCount() - 1))
                .addValues(new double[]{0, 1})
                .build();


        F = boundaryConditions.applyBoundaryConditionsToRightPart(K, F);
        K = boundaryConditions.applyBoundaryConditionsToLeftPart(K);

    }


    public double[] solve(){
        final double Tmax = 1;
        init();
        DecompositionSolver solver = new LUDecomposition(K).getSolver();
        RealVector X = solver.solve(F);
        return X.toArray();
    }
}
