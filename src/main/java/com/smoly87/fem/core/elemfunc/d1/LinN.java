/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core.elemfunc.d1;

import com.smoly87.fem.core.ElemFunc;
import com.smoly87.fem.core.ElemFuncType;
import com.smoly87.fem.core.Element;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;

import java.util.function.BiFunction;
/**
 *
 * @author Andrey
 */
public class LinN extends ElemFunc implements UnivariateFunction{
    protected int elemNum;

    public LinN(Element elem) {
        super(elem);
        funcsCount = 2;
    }
    
    @Override
    public double F(double[] c, int funcNum) {
        double res = 0;
        double x = c[0];
        Element1d elem1d = (Element1d)elem;
        double h = elem1d.getH();
        
        switch (funcNum){
            case 0:
                res = 1 - x/h;
                break;
            case 1:
                res = x/h;
                break;
        }
        
        return res;
    }

    @Override
    public double dFdx(double[] c, int funcNum) {
        double res = 0;
        double x = c[0];
        Element1d elem1d = (Element1d)elem;
        double h = elem1d.getH();
        
        switch (funcNum){
            case 0:
                res = -1/h;
                break;
            case 1:
                res = 1/h;
                break;
        }
        
        return res;
    }


    @Override
    public double integrate(ElemFuncType type1, ElemFuncType type2, int l, int m ) {
         Element1d curElem = (Element1d) elem;
         
         double minV = 0;
         double maxV = curElem.getH();
         double J = 1;        
         
         setCurElemParams(elem, type1, type2, l, m);
      
         SimpsonIntegrator integrator = new SimpsonIntegrator();
         return J * integrator.integrate(20, this, minV, maxV);         
    }

    protected double applyFuncCall(BiFunction f, int argNum, double x){
        int funcNum = argNum == 0 ? funcParams.getFuncNum1() : funcParams.getFuncNum2();
        return (double) f.apply(new double[]{x}, funcNum);
    }
    
    @Override
    public double value(double x) {
       double v1 = applyFuncCall(f1, 0, x); 
       double v2 = applyFuncCall(f2, 1, x); ; 
       return v1 * v2;
    }
    
}
