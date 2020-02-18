/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.tasks;

import com.google.inject.Inject;
import com.smoly87.fem.core.*;
import com.smoly87.rendering.Body;
import com.smoly87.rendering.Java2DRenderer;
import com.smoly87.rendering.SceneRender;
import com.smoly87.rendering.ScreenConfig;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.ode.SecondOrderDifferentialEquations;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.Pair;
import static  java.lang.Math.sin;
import static  java.lang.Math.PI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Andrey
 */
public class WaveEquation1d extends Task {
    protected int elemNum;
    protected SceneRender sceneRender;
    protected RealMatrix C;

    private final double L = 5d;
    @Inject
    public WaveEquation1d(SceneRender sceneRender) {
        this.sceneRender = sceneRender;
    }
    protected double Klm(Element elem, Integer l, Integer m){
        //Weak form
        return elem.getElemFunc().integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, l, m);
    }
    protected double Clm(Element elem, Integer l, Integer m){
        return elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.F, l, m);
    }
    protected RealVector getInitialConditions(ArrayList<Vector> points){
        int N = points.size();
        double[] values = new double[N];

        for(int i = 1; i < N -1; i++){
            Vector point = points.get(i);
            double x = point.getCoordinates()[0];

            values[i] = sin((x/L) * PI);
            //System.out.println(values[i]);
        }

        return new ArrayRealVector(values);
    }

    protected RealVector fillForces(RealVector F) {
        final double g = 0.5;
       return new ArrayRealVector(Arrays.stream(F.toArray()).map(v -> g).toArray()) ;
       /*F.setEntry(40,1);
       return  F;*/
    }

    protected void init(int elemNum) {
        mesh = SimpleMeshBuilder.create1dLineMesh(elemNum,L, true);
        int N = mesh.getNodesCount();

        this.initMatrixes(N);
        
        K = fillGlobalStiffness(K, this::Klm).scalarMultiply(0.01);
        C = new Array2DRowRealMatrix(N, N);
        C = fillGlobalStiffness(C, this::Clm);
        F = fillForces(F); //No need forces!
        double[] QBound = new double[]{0, 0}; // Fix nodes position on the both end of the string.
        Integer[] boundNodes = new Integer[]{0, mesh.getNodesCount() - 1};
        boundaryConitions = new BoundaryConditions(QBound, boundNodes);

        F = this.applyBoundaryConditionsToRightPart(K, F, boundaryConitions);
        K = this.applyBoundaryConditionsToLeftPart(K,  boundaryConitions);
        C = this.applyBoundaryConditionsToLeftPart(C,  boundaryConitions);

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
                //System.out.println(t + ";" + y[14]);
                //visualizeStepFromSolution(t, y);
            }
        };
        return stepHandler;
    }

    public void  solve(int elemNum){
        final double Tmax = 5d;
        init(elemNum);
       // solveTimeProblem(createStepHandler(), getInitialConditions(mesh.getPoints()).toArray(),0, Tmax, 0.001 );
       // solveTimeProblemCustomIntegrator(this::visualizeStepFromSolution, getInitialConditions(mesh.getPoints()).toArray(),0,Tmax, 0.01);
     /*  DecompositionSolver solver = new LUDecomposition(K).getSolver();
        RealVector X = solver.solve(F);
        ;
        visualizeStaticSolution(restoreBoundary(X.toArray(), boundaryConitions));*/
        K = MatrixUtils.inverse(C).multiply(K).scalarMultiply(-1); // df/dx = A*x - form of ode sys
        solveTimeProblemCustomIntegrator(this::visualizeStepFromSolution, getInitialConditions(mesh.getPoints()).toArray(),0,Tmax, 0.01);

    }

    private void visualizeStepFromSolution(double t, Double[] y) {
        List<Vector2D> vertexes = new ArrayList<>();
        int N = y.length / 2; // First N are velocities since the system was converted from the second order to first.
        for(int i = 1; i < N - 1; i++) {
            double xi = mesh.getPoints().get(i).getCoordinates()[0];
            Vector2D graphPoint = new Vector2D(xi, y[ i]);
            vertexes.add(graphPoint);
        }
        Body graph = new Body();
        graph.setVertexes(vertexes);
        sceneRender.clear();
        sceneRender.renderBodies(List.of(graph), Color.BLUE);
        sceneRender.redraw();
    }


    private void visualizeStaticSolution(double[] y) {
        List<Vector2D> vertexes = new ArrayList<>();
        int N = y.length ; // First N are velocities since the system was converted from the second order to first.
        for(int i = 0; i < N; i++) {
            double xi = mesh.getPoints().get(i).getCoordinates()[0];
            Vector2D graphPoint = new Vector2D(xi, y[i]);
            vertexes.add(graphPoint);
        }
        Body graph = new Body();
        graph.setVertexes(vertexes);
        sceneRender.clear();
        sceneRender.renderBodies(List.of(graph), Color.BLUE);
        sceneRender.redraw();
    }
}
