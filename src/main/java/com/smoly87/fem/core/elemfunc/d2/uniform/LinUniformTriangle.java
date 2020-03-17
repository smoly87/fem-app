/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core.elemfunc.d2.uniform;

import com.smoly87.fem.core.ElemFunc2d;
import com.smoly87.fem.core.ElemFuncType;
import com.smoly87.fem.core.Element;
import com.smoly87.fem.core.Vector;
import java.util.function.BiFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import org.apache.commons.math3.util.CombinatoricsUtils;
/**
 *
 * @author Andrey
 */
public class LinUniformTriangle extends ElemFunc2d implements UnivariateFunction{
    protected double det;

    public double[] getP0() {
        return p0;
    }

    public double[] getP1() {
        return p1;
    }

    public double[] getP2() {
        return p2;
    }

    public double[] getA() {
        return a;
    }

    public double[] getB() {
        return b;
    }

    public double[] getG() {
        return g;
    }
    
    protected double[] p0 ;
    protected double[] p1 ;
    protected double[] p2 ;
    
    protected double[] a;
    protected double[] b;
    protected double[] g;
    protected RealMatrix detMat;
    protected DecompositionSolver coofSolver;
    protected LinUniformTriangleWrapper innerInteg;
    protected SimpsonIntegrator integrator;
    protected SimpsonIntegrator integrator2;
    protected double L1;
    protected double J;
    protected RealMatrix Jac;
    protected RealMatrix derivativesCoofs;
    protected Array2DRowRealMatrix dLKoofs; 
    public LinUniformTriangle(Element element) {
        super(element);
         funcsCount = 3;
        
        p0 = pointValues(element, 0);
        p1 = pointValues(element, 1);
        p2= pointValues(element, 2);
        countJ();
        
        detMat = new Array2DRowRealMatrix(new double[][]{
            {1, p0[0], p0[1]},
            {1, p1[0], p1[1]},
            {1, p2[0], p2[1]},
        });
        coofSolver = new QRDecomposition(detMat).getSolver();
        
        det = countDet(element);
         
        a = new double[3];
        b = new double[3];
        g = new double[3]; 
        
        countCoofs(0);
        countCoofs(1);
        countCoofs(2);
        
        integrator = new SimpsonIntegrator();
         integrator2 = new SimpsonIntegrator();
        innerInteg = new LinUniformTriangleWrapper(this);
    }
    
    protected double[] pointValues(Element element, int nodeInd){
        int ind = element.getNodesList().get(nodeInd);
        Vector point = element.getMesh().getPoints().get(ind);
        return point.getCoordinates();
    }
    
    protected double countDet(Element element){
       return new  LUDecomposition(detMat).getDeterminant();
    }
    
    protected void countCoofs(int funcNum){
        double[] bv = new double[3];
        bv[funcNum] = 1;
        
        RealVector bRv = new ArrayRealVector(bv);
        RealVector koofsV = coofSolver.solve(bRv);
        double[] koofs = koofsV.toArray();
        
        a[funcNum] = koofs[0];
        b[funcNum] = koofs[1];
        g[funcNum] = koofs[2];
    }
    
  
    public double FA(double[] c, int funcNum) {
        double x = c[0];
        double y = c[1];
        return a[funcNum] + b[funcNum]*x + g[funcNum]*y;
    }

  

  
    public double dFdxA(double[] c, int funcNum) {
       return b[funcNum];
    }
    
  
    public double dFdyA(double[] c, int funcNum) {
       return g[funcNum];
    }
    
    protected int summ(int[] arr){
        int s = 0;
        for(int i = 0; i < arr.length; i++){
            s += arr[i];
        }
        return s;
    }
    
    protected int facProd(int[] arr){
        int s = 1;
        for(int i = 0; i < arr.length; i++){
            s *=  CombinatoricsUtils.factorial(arr[i]);
        }
        return s;
    }
    
    protected double LIntegral(int[] v ){
        double denom = CombinatoricsUtils.factorial(summ(v) + 2);
        double sq = (double)facProd(v)* det  / denom ;
        return sq;
    }
    public double integrateLCoordFormula( ElemFuncType type1, ElemFuncType type2, int l, int m) {
        double sq = 0;
        /*if((type1 == ElemFuncType.F || type2 == ElemFuncType.F) && (l == 0 || m ==0)){
            if(l != 0){
                int t = l;
                l = m;
                m = t;
            }
            switch(m){
                case 0:
                    sq = LIntegral(new int[3]) - 4* LIntegral(new int[]{1, 0, 0}) 
                       + 2 * LIntegral(new int[]{1, 1, 0})+ 2 * LIntegral(new int[]{2, 0, 0});
                    break;
                case 1: case 2:
                    sq =  LIntegral(new int[]{1, 0, 0}) - LIntegral(new int[]{1, 1, 0}) 
                        - LIntegral(new int[]{2, 0, 0});
                    break;
                    
            }
             return sq;
        }*/
        
        int[] v = new int[3];
        if(type1 == ElemFuncType.F) v[l]++;
        if(type2 == ElemFuncType.F) v[m]++;
       
        sq = LIntegral(v);
        return sq;
    }
    
   /* public double integrateabgFormula(ElemFuncType type1, ElemFuncType type2, int l, int m){
        
    }*/
    
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

    protected void countJ(){
        Jac = new Array2DRowRealMatrix(new double[][]{
            { -p0[0]+p1[0], -p0[1]+p1[1]},
            { -p0[0]+p2[0], -p0[1]+p2[1]},
            
        });
        
         dLKoofs = new Array2DRowRealMatrix(new double[][]{
            {-1, 1, 0},
            {-1, 0, 1},
        });
        
        
        LUDecomposition decomp = new  LUDecomposition(Jac);
        J = decomp.getDeterminant();
        RealMatrix JInv = decomp.getSolver().getInverse();
        derivativesCoofs = JInv.multiply(dLKoofs);
       // System.out.println("8");
    }
    
  /* @Override
   public double F(double[] c, int funcNum) {
        double x = c[0];
        double y = c[1];
        return a[funcNum] + b[funcNum]*x + g[funcNum]*y;
    }

    @Override
    public double dFdx(double[] c, int funcNum) {
       return b[funcNum];
    }
    
    @Override
    public double dFdy(double[] c, int funcNum) {
       return g[funcNum];
    }*/
    @Override
    public double F(double[] c, int funcNum) {
        return c[funcNum] ;
    }

    @Override
    public double dFdx(double[] c, int funcNum) {
        //return derivativesCoofs.getEntry(0, funcNum);
        return derivativesCoofs.getEntry(0, funcNum);
    }
    
    @Override
    public double dFdy(double[] c, int funcNum) {
        //return derivativesCoofs.getEntry(1, funcNum);
         return derivativesCoofs.getEntry(1, funcNum);
    }
}
