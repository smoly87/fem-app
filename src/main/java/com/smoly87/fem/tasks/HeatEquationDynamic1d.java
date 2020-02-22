/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.tasks;

import com.google.inject.Inject;
import com.smoly87.fem.core.*;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
// This task is from page 108 Zenkevic
// Try to eliminate rows with boundary conditions end estimate accuracy.
public class HeatEquationDynamic1d extends Task {
    protected RealMatrix C;
    @Inject
    public HeatEquationDynamic1d(){
    }

    protected double Klm(Element elem, Integer l, Integer m){
        return elem.getElemFunc().integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, l, m);
    }

    protected double Clm(Element elem, Integer l, Integer m){
        return elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.F, l, m);
    }
    
    protected void init(int elemNum) {
        mesh = SimpleMeshBuilder.create1dLineMesh(elemNum,1, true);
        int N = mesh.getNodesCount();
        this.initMatrixes(N);
        
        K = fillGlobalStiffness(K, this::Klm);
        C = new Array2DRowRealMatrix(N, N);
        C = fillGlobalStiffness(C, this::Clm);

        double[] QBound = new double[]{0, 0};
        Integer[] boundNodes = new Integer[]{0, N - 1};
        boundaryConitions = new BoundaryConditions(QBound, boundNodes);

        // need to keep the certain order.
        F = this.applyBoundaryConditionsToRightPart(K, F, boundaryConitions);
        K = this.applyBoundaryConditionsToLeftPart(K, boundaryConitions);
        C = this.applyBoundaryConditionsToLeftPart(C, boundaryConitions);

    }

    public void solve(int elemNum){
        final double Tmax = 1;
        init(elemNum);
        /*DecompositionSolver solver = new LUDecomposition(K).getSolver();
        RealVector X = solver.solve(F);*/
        K = MatrixUtils.inverse(C).multiply(K).scalarMultiply(-1); // df/dx = A*x - form of ode sys
        //solveTimeProblemFirstOrder(createStepHandler(), getInitialConditions(mesh.getPoints()).toArray(),0, Tmax, 0.01 );
        solveTimeProblemFirstOrder(createStepHandler(), getInitialConditions(mesh.getPoints()).toArray(),0,Tmax, 0.01);

        //return X.toArray();
    }

    protected StepHandler createStepHandler() {
        StepHandler stepHandler = new StepHandler() {
            @Override
            public void init(double v, double[] doubles, double v1) {

            }

            @Override
            public void handleStep(StepInterpolator stepInterpolator, boolean b) throws MaxCountExceededException {
                double   t = stepInterpolator.getCurrentTime();
                double[] y = stepInterpolator.getInterpolatedState();
                System.out.println(t + ";" + y[0]);
                //visualizeStepFromSolution(t, y);
            }
        };
        return stepHandler;
    }
    protected RealVector getInitialConditions(ArrayList<Vector> points) {
        int N = points.size();
        double[] values = new double[N - boundaryConitions.getNodesCount()];
        values[0] = 1;
        return new ArrayRealVector(values);
    }
    private void visualizeStepFromSolution(double t, Double[] y) {
        System.out.println(t +";" + y[0]);
    }
}
