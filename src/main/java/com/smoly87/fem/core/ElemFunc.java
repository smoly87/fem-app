/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core;

import com.smoly87.fem.core.elemfunc.FuncParams;
import java.util.function.BiFunction;

/**
 *
 * @author Andrey
 */
public abstract class ElemFunc {
   protected int funcsCount; 

    public int getFuncsCount() {
        return funcsCount;
    }
   public  double Id(double[] c, int funcNum){
       return 1.0;
   }
   public abstract double F(double[] c, int funcNum);
   public abstract double dFdx(double[] c, int funcNum);
   
   public abstract double integrate( ElemFuncType type1, ElemFuncType type2,int l, int m);
   
    
   protected BiFunction<double[], Integer, Double> f1;
   protected BiFunction<double[], Integer, Double> f2;
   protected FuncParams funcParams;
   
   protected Element elem;

    public ElemFunc(Element elem) {
        this.elem = elem;
        funcParams = new FuncParams();
    }
   
   protected void setCurElemParams(Element elem, ElemFuncType type1, ElemFuncType type2,int l, int m ){
         f1 = getFuncRef(type1);
         f2 = getFuncRef(type2);
                
         funcParams.setCurElem(elem);
         funcParams.setFuncNum1(l);
         funcParams.setFuncNum2(m);
   }
   
   protected BiFunction<double[], Integer, Double> getFuncRef(ElemFuncType type){
        BiFunction<double[], Integer, Double>  res = null;
        switch(type){
            case I:
                res = this::Id;
                break;
            case F:
                res = this::F;
                break;
            case dFdx:
                res = this::dFdx;
                
        }
        return res;
    }
    
    public double FA(double[] c, int funcNum){
        double[] lc = absCoordToLocal(c);
        return F(lc, funcNum);
    }
    
    protected double[] absCoordToLocal(double[]c ){
        return c;
    }    
   
}
