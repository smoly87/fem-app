/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core.elemfunc.d2.lin;

import com.smoly87.fem.core.ElemFunc2d;
import com.smoly87.fem.core.ElemFuncType;
import com.smoly87.fem.core.Element;
import com.smoly87.fem.core.Vector;
import com.smoly87.fem.core.elemfunc.d2.uniform.LinUniformTriangleWrapper;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.*;

import java.util.function.BiFunction;

/**
 *
 * @author Andrey
 */
public class LinTriangle extends ElemFunc2d implements UnivariateFunction {
    private static final int IND_I = 0;
    private static final int IND_J = 1;
    private static final int IND_K = 2;

    private static final int IND_ALPHA = 0;
    private static final int IND_BETTA = 1;
    private static final int IND_GAMMA = 2;

    protected double[] p0 ;
    protected double[] p1 ;
    protected double[] p2 ;


    protected double[][] derivativeCoofs = new double[3][3];

    protected Vector2D[] baracentricVertexes = new Vector2D[3];
    Vector2D A; // Vertexes of triangle
    Vector2D B;
    Vector2D C;

    protected SimpsonIntegrator integrator;
    protected SimpsonIntegrator integrator2;
    protected LinTriangleWrapper innerInteg;
    protected double L1;
    protected double J;

    public LinTriangle(Element elem) {
        super(elem);

        p0 = pointValues(elem, 0);
        p1 = pointValues(elem, 1);
        p2 = pointValues(elem, 2);
        A = new Vector2D(p0);
        B = new Vector2D(p1);
        C = new Vector2D(p2);
        calculateVertexesInBaraxentricCoords();
        derivativeCoofs[IND_I] = calculateDerivativeCoofs(IND_I);
        derivativeCoofs[IND_J] = calculateDerivativeCoofs(IND_J);
        derivativeCoofs[IND_K] = calculateDerivativeCoofs(IND_K);
        J = 1;// calculateJacobian(A, B, C);

        integrator = new SimpsonIntegrator();
        integrator2 = new SimpsonIntegrator();
        innerInteg = new LinTriangleWrapper(this);
    }


    @Override
    public double dFdy(double[] c, int funcNum) {
        return derivativeCoofs[funcNum][IND_GAMMA];
    }

    @Override
    public double F(double[] c, int funcNum) {
        Vector3D vecCoords = new Vector3D(new double[]{1d, c[0], c[1]});
        Vector3D vecCoofs = new Vector3D(derivativeCoofs[funcNum]);
        double value = vecCoords.dotProduct(vecCoofs);
        return value;
    }

    @Override
    public double dFdx(double[] c, int funcNum) {
        return derivativeCoofs[funcNum][IND_BETTA];
    }


    private double[] pointValues(Element element, int nodeInd){
        int ind = element.getNodesList().get(nodeInd);
        Vector point = element.getMesh().getPoints().get(ind);
        return point.getCoordinates();
    }

    protected double calculateJacobian(Vector2D A, Vector2D B, Vector2D C) {
        Vector2D AB = B.subtract(A);
        Vector2D AC = C.subtract(A);

        double[][] Jacobian = new double[][] {
                {AB.getX(), AC.getX()},
                {AB.getY(), AC.getY()},
        };
        double J = new LUDecomposition(new Array2DRowRealMatrix(Jacobian)).getDeterminant();
        return J;
    }

    private void calculateVertexesInBaraxentricCoords() {


        baracentricVertexes[0] = convertToBaracentric(A, A, B, C);
        baracentricVertexes[1] = convertToBaracentric(B, A, B, C);
        baracentricVertexes[2] = convertToBaracentric(C, A, B, C);

    }

    // Returns two vertexes j,k to keep always clockwise order (indeed cyclic traverse)
    private int[] getNextVertexes(int i) {
        int[] res = new int[2];
        for(int m = 0; m < 2; m++) {
            int p = i + m + 1;
            if (p > 2) {
                p = p % 3;
            }
            res[m] = p;
        }
        return res;
    }

    private double[] calculateDerivativeCoofs(int i) {
        int[] adjVertexes = getNextVertexes(i);
        int j = adjVertexes[0];
        int k = adjVertexes[1];

        RealMatrix K = new Array2DRowRealMatrix(new double[][]{
                {1d,baracentricVertexes[i].getX(),baracentricVertexes[i].getY() },
                {1d,baracentricVertexes[j].getX(),baracentricVertexes[j].getY() },
                {1d,baracentricVertexes[k].getX(),baracentricVertexes[k].getY() },

        });
        DecompositionSolver solver = new LUDecomposition(K).getSolver();
        RealVector solution = solver.solve(new ArrayRealVector(new double[]{1d, 0d, 0d}));
        return solution.toArray();
    }

    /**
     *
     * @param P Point for transformation
     * @param A First vertex of triangle
     * @param B Second vertex of triangle
     * @param C Third vertex of the triangle
     * @return
     */
    private Vector2D convertToBaracentric(Vector2D P, Vector2D A, Vector2D B, Vector2D C) {
        // Corresponding article is https://habr.com/ru/post/249467/
        Vector2D AB = B.subtract(A);
        Vector2D AC = C.subtract(A);
        Vector2D PA = P.subtract(A);

        RealMatrix K = new Array2DRowRealMatrix(new double[][]{
                {AB.getX(), AC.getX()},
                {AB.getY(), AC.getY()},
        });
        DecompositionSolver solver = new LUDecomposition(K).getSolver();
        RealVector rightPart = new ArrayRealVector(new double[] {PA.getX(), PA.getY()  }, false);
        RealVector solution = solver.solve(rightPart);
        return new Vector2D(solution.getEntry(0), solution.getEntry(1));
    }

    // Integration stuff could be moved to one separate class or base ?
    @Override
    public double integrate( ElemFuncType type1, ElemFuncType type2, int l, int m) {



         /*setCurElemParams(elem, type1, type2, l, m);
         return  integrateLCoordFormula(type1, type2, l,m);*/
        /* double JL = 1.0;
         if(type1 == ElemFuncType.F || type1 == ElemFuncType.I) JL = J;*/
        setCurElemParams(elem, type1, type2, l, m);
        double Integ = integrator.integrate(20, this, 0, 0.999999);
        return  J * Integ;
       /*double[] x =  new double[2];
       double v1 = applyFuncCall(f1, 0, x);
       double v2 = applyFuncCall(f2, 1, x);
       //TODO: Figure out multiplier 0.5 is necessary or not.
       return 0.5*det * v1 *v2;*/
    }

    protected double applyFuncCall(BiFunction f, int argNum, double[] coords){
        int funcNum = argNum == 0 ? funcParams.getFuncNum1() : funcParams.getFuncNum2();
        //This is done to save uniform approach
        //But absolutely obviously that it could e released by just multiple numeric coofs
        return (double) f.apply(coords, funcNum);
    }

    @Override
    public double value(double L1) {
        this.L1 = L1;
        return integrator2.integrate(20, innerInteg, 0, 1 - L1 );
    }

    public double innerValue(double L2){
        if(L1 + L2 > 1.0001) {
            System.out.println("L1 + L2 > 1");
        }
        double[] args = new double[]{1-L1-L2,L1, L2};
        double v1 = applyFuncCall(f1, 0, args);
        double v2 = applyFuncCall(f2, 1, args);
        return v1*v2;
    }

}
