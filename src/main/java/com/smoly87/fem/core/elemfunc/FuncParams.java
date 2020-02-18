/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core.elemfunc;

import com.smoly87.fem.core.Element;

/**
 *
 * @author Andrey
 */
public class FuncParams {
    protected double h;
    protected int funcNum1;
    protected int funcNum2;
    protected Element curElem; 

    public Element getCurElem() {
        return curElem;
    }

    public void setCurElem(Element curElem) {
        this.curElem = curElem;
    }
    
    public int getFuncNum1() {
        return funcNum1;
    }

    public void setFuncNum1(int funcNum1) {
        this.funcNum1 = funcNum1;
    }

    public int getFuncNum2() {
        return funcNum2;
    }

    public void setFuncNum2(int funcNum2) {
        this.funcNum2 = funcNum2;
    }
    

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

   
}
