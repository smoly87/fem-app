/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.smoly87.fem.tasks.CustomRungeCutta;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.ode.FirstOrderConverter;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.util.Pair;

/**
 *
 * @author Andrey
 */
public class Task {
    protected RealMatrix K;
    protected RealVector F;
    protected Mesh mesh;
    protected BoundaryConditions boundaryConitions;
 
    public BoundaryConditions getBoundaryConitions() {
        return boundaryConitions;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    protected final double MIN_ELEM = 10^-16;
    
    protected void initMatrixes(int N){
        K = new Array2DRowRealMatrix(N, N);
        F = new ArrayRealVector(N);
    }
    
    protected double[][] scalarMultiply(double[][] arr, double d){
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr[i].length; j++){
                arr[i][j] = d * arr[i][j]; 
            }
        }
        return arr;
    }

    protected  RealVector applyBoundaryConditionsToRightPart(RealMatrix KG, RealVector FG, BoundaryConditions boundaryConditions) {
        int N = KG.getRowDimension();
        for(int k = 0; k < boundaryConditions.getNodesCount(); k++){
            int i = boundaryConditions.getPointIndex(k);
            double Qbound = boundaryConditions.getBoundaryValue1d(i);
            for(int j = 0; j < N; j++){
                if(j != i){
                    FG.addToEntry(j, -KG.getEntry(j, i) * Qbound);
                }
            }
        }
        int B = boundaryConditions.getNodesCount();
        Set<Integer> boundaryIndexes = boundaryConditions.getBoundIndexes().stream().collect(Collectors.toSet());
        double[] FN = new double[N - B];
        int r = 0;
        for(int i = 0; i < N; i++) {
            if (boundaryIndexes.contains(i) ) continue;
            FN[r] = F.getEntry(i);
            r++;
        }
        return new ArrayRealVector(FN);
    }

    protected RealMatrix applyBoundaryConditionsToLeftPart(RealMatrix KG, BoundaryConditions boundaryConditions){
        int N = KG.getRowDimension();
        int B = boundaryConditions.getNodesCount();
        Set<Integer> boundaryIndexes = boundaryConditions.getBoundIndexes().stream().collect(Collectors.toSet());
        double[][] KgN = new double[N - B][N - B];
        int r = 0;
        int c = 0;
        for(int i = 0; i < N; i++) {
            if (boundaryIndexes.contains(i) ) continue;
            c = 0;
            for (int j = 0; j < N; j++) {
                if (boundaryIndexes.contains(j)) continue;
                KgN[r][c] = KG.getData()[i][j];
                c++;
            }
            r++;
        }
        return new Array2DRowRealMatrix(KgN);
    }

    protected RealMatrix removeElemsForBoundConds(RealMatrix A, BoundaryConditions boundaryConditions) {
        for(int innoundInd: boundaryConditions.getBoundIndexes()) {
            A.setRow(innoundInd, new double[A.getColumnDimension()]);
            A.setEntry(innoundInd, innoundInd, 1d);
        }
        return A;
    }

    protected RealVector removeElemsForBoundConds(RealVector F, BoundaryConditions boundaryConditions) {
        for(int innoundInd: boundaryConditions.getBoundIndexes()) {
            F.setEntry(innoundInd, boundaryConditions.getBoundaryValue1d(innoundInd));
        }
        return F;
    }
    
    protected double[][] fillStiffnessMatrix(Element elem, KlmFunction KLMFunc){
      int N = elem.nodesList.size();
    
      double[][] KLoc = new double[N][N];
      for(int k = 0; k < N;k++){
         int c = k;
         for(int r = 0; r < N-k;r++){
            double v = KLMFunc.apply(elem, r, c);//elem.getElemFunc().integrate( type1, type2, r, c);
            KLoc[r][c] = v;
            KLoc[c][r] = KLoc[r][c]; 
            c++;
         } 
      }
     
      return KLoc;
    }
    
    protected RealMatrix arrangeInGlobalStiffness(RealMatrix M, double[][] KLoc, List<Integer> numsList){
        int N = numsList.size();
        for(int l = 0; l < N; l++){
            for(int m = 0; m < N; m++){
                int i = numsList.get(l);
                int j = numsList.get(m);  
                M.addToEntry(i, j, KLoc[l][m]);
            }
        }
        return M;
    }
    
    
    protected RealMatrix arrangeSubMatrix(RealMatrix M, double[][] subMatrix, int row, int col){
        int N = subMatrix.length;
        M.setSubMatrix(subMatrix, row * N, col * N);
        
        return M;
    }
    protected RealMatrix arrangeSubMatrix(RealMatrix M, RealMatrix subMatrix, int row, int col){
        M.setSubMatrix(subMatrix.getData(), row, col);
        return M;
    }
    
    protected RealMatrix buildSystem(Mesh mesh, SysBlockBuilder blockBuilder, int blockSize){
        ArrayList<Element> elems = mesh.getElements();
        int Nb = mesh.getNodesCount();
        RealMatrix K = new Array2DRowRealMatrix(Nb * blockSize, Nb * blockSize);
        for(int i = 0; i < elems.size(); i++){
            Element elem = elems.get(i);
            ArrayList<Integer> nodesList = elem.getNodesList();
            int N = nodesList.size();
            for(int l = 0; l < N; l++){
                for(int m = 0; m < N; m++){
                    int gl = nodesList.get(l);
                    int gm = nodesList.get(m);
                    RealMatrix blockCell =  blockBuilder.apply(elem, l, m);
                    K = this.arrangeSubMatrix(K, blockCell, gl, gm);
                }
            }
        }
        return K;
    }
    
    protected RealMatrix assembleBlockDiag(int N,  int Tsteps,  RealMatrix firstRowMatr, RealMatrix rowMatr, RealMatrix lastRowMatr){
        int gs = N * Tsteps;
        RealMatrix G = new Array2DRowRealMatrix(gs, gs);
       
        for(int r = 0; r < Tsteps; r++ ){ 
            
            RealMatrix cRow = null;
            int rFrom = r * N;
            int colFrom = r < 2 ? 0 : r * N;
             
            if( r == 0){
                cRow = firstRowMatr;
            } else if (r == Tsteps - 1){
                cRow = lastRowMatr;
            } else{
                cRow = rowMatr;
            }
 
            G.setSubMatrix(cRow.getData(), rFrom, colFrom);
        }
        
        return G;
    }
    
    public static double[] restoreBoundary(double[]X,  BoundaryConditions boundaryConditions){
        int BN = boundaryConditions.getNodesCount();
        int N = X.length + BN;
        double[] R = new double[N];
        int l = 0;
        int pointInd = boundaryConditions.getPointIndex(0);
        // From zero to first bound point
        System.arraycopy(X, 0, R, 0, pointInd);
        l = pointInd;
        for(int k = 0; k < BN - 1; k++){
            pointInd = boundaryConditions.getPointIndex(k);
            R[pointInd] = boundaryConditions.getBoundaryValue1d(k);
            int partLen = boundaryConditions.getPointIndex(k + 1) - pointInd - 1;
            System.arraycopy(X, l , R, pointInd+1,  partLen);
            l += partLen;
        }
        
       // Part from last index to end of X also should be considered.
        pointInd = boundaryConditions.getPointIndex(BN - 1);
        R[pointInd] = boundaryConditions.getBoundaryValue1d(BN - 1);
        System.arraycopy(X, l, R, pointInd+1, N - 1 - pointInd);
        
        return R;
    }

     protected double[][] convertSolution(RealVector X, BoundaryConditions boundaryCond, int timeSteps ){
        double [] data = X.toArray();
        int BN = boundaryConitions.getNodesCount();
        int N = data.length / timeSteps  ;
        double[][] res = new double[timeSteps + 1][N + BN];
        for(int t = 0; t < timeSteps; t++){
            double[] values = new double[N];
            System.arraycopy(data, t * N, values, 0, N);
            values = restoreBoundary(values, boundaryConitions); 
            res[t + 1] = values;
        }
        
        res[0] = restoreBoundary(boundaryCond.getBoundNodes(), boundaryConitions);
        
        return res ;
    }
     
    protected RealMatrix fillGlobalStiffness(RealMatrix M, KlmFunction klmFunc) {
        ArrayList<Element> elements = mesh.getElements();
        for (int i = 0; i < elements.size(); i++) {
            Element elem = elements.get(i);

            double[][] MLoc = fillStiffnessMatrix(elem, klmFunc);
            M = this.arrangeInGlobalStiffness(M, MLoc, elem.getNodesList());
        }
        
        return M;
    } 
    
    protected RealMatrix fillGlobalStiffness(RealMatrix M, double[][] MLoc) {
        List<Element> elements = mesh.getElements();
        for (int i = 0; i < elements.size(); i++) {
            Element elem = elements.get(i);
            M = this.arrangeInGlobalStiffness(M, MLoc, elem.getNodesList());
        }
        
        return M;
    }

    protected double[][] identityMatrixData(int N) {
        double[][] I = new double[N][N];
        for(int i = 0; i < N; i++) {
            I[i][i] = 1;
        }
        return I;
    }

    protected void solveTimeProblem_(StepHandler stepHandler, double[] Y0, double tMin, double tMax, double tStep) {

        OdeSystem2 odeSystem2 = new OdeSystem2(K, F);
        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(tStep);
        integrator.addStepHandler(stepHandler);

        /*ExpandableStatefulODE expandableODE = new ExpandableStatefulODE(odeSystem);
        expandableODE.setTime(tMin);
        expandableODE.setPrimaryState(YG.toArray());*/

        integrator.integrate(new FirstOrderConverter(odeSystem2),0, Y0,tMax,Y0);

    }
    protected void solveTimeProblemFirstOrder(StepHandler stepHandler, double[] Y0, double tMin, double tMax, double tStep) {

        OdeSystem odeSystem = new OdeSystem(K, F);
        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(tStep);
        integrator.addStepHandler(stepHandler);
        double Y[] = new double[K.getRowDimension()];
        integrator.integrate(odeSystem,0, Y0,tMax,Y);
    }

    protected void solveTimeProblemSecondOrder(StepHandler stepHandler, double[] Y0, double tMin, double tMax, double tStep) {
        int N = K.getRowDimension();
        RealMatrix G = new Array2DRowRealMatrix(N * 2, N * 2); // To convert from second order to first
        double[][] I =  identityMatrixData(N);
        G.setSubMatrix(I,0,N);
        G.setSubMatrix(K.getData(), N,0);

        RealVector FG = new ArrayRealVector(N * 2);
        FG.setSubVector(N, F);
        RealVector YG = new ArrayRealVector(N * 2);
        YG.setSubVector(N, new ArrayRealVector(Y0));
        //YG.setSubVector(0, new ArrayRealVector(Y0));

        OdeSystem odeSystem = new OdeSystem(G, FG);
       // FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(tStep);
        FirstOrderIntegrator integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
        integrator.addStepHandler(stepHandler);
        double[] Y = new double[N * 2];

        integrator.integrate(odeSystem,0, YG.toArray(),tMax,Y);
    }

    private String showMatrix(RealMatrix A) {
        List<String> rows = new ArrayList<>(A.getRowDimension());
        for(int i = 0; i < A.getRowDimension();i++) {
            List<String> values = new ArrayList<>(A.getColumnDimension());
            for(int j = 0 ; j < A.getColumnDimension(); j++) {
                values.add(Double.toString(A.getEntry(i,j)));
            }
            rows.add(values.stream().collect(Collectors.joining(",")));
        }
        return rows.stream().collect(Collectors.joining("\n"));
    }

    protected void solveTimeProblemCustomIntegrator(BiConsumer<Double, Double[]> stepHandler, double[] Y0, double tMin, double tMax, double tStep) {
        int N = K.getRowDimension();
        RealMatrix G = new Array2DRowRealMatrix(N * 2, N * 2); // To convert from second order to first
        double[][] I =  identityMatrixData(N);
        G.setSubMatrix(I,0,N);
        G.setSubMatrix(K.getData(), N,0);

        RealVector FG = new ArrayRealVector(N * 2);
        FG.setSubVector(0, F);
        RealVector YG = new ArrayRealVector(N * 2);
        YG.setSubVector(0, new ArrayRealVector(Y0));
        //YG.setSubVector(0, new ArrayRealVector(Y0));

        OdeSystem odeSystem = new OdeSystem(G, FG);
        CustomRungeCutta integrator = new CustomRungeCutta();
        integrator.setStepHandler(stepHandler);
        integrator.integrate(odeSystem, YG.toArray(), tStep, tMin, tMax);

    }
    /*protected void solveTimeProblemFirstOrder(StepHandler stepHandler, double[] Y0, double tMin, double tMax, double tStep) {
        int N = K.getRowDimension();


        OdeSystem odeSystem = new OdeSystem(K, F);
        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(tStep);
        integrator.addStepHandler(stepHandler);
        double[] Y = new double[Y0.length];
        integrator.integrate(odeSystem,   tMin,Y0,  tMax, Y);

    }*/

    protected void solveTimeProblemFirstOrderCustom(BiConsumer<Double, Double[]> stepHandler, double[] Y0, double tMin, double tMax, double tStep) {
        int N = K.getRowDimension();


        OdeSystem odeSystem = new OdeSystem(K, F);
        CustomRungeCutta integrator = new CustomRungeCutta();
        integrator.setStepHandler(stepHandler);
        integrator.integrate(odeSystem, Y0, tStep, tMin, tMax);

    }
}
