/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core.elemfunc.d2.lin;

import com.smoly87.fem.core.elemfunc.d2.uniform.LinUniformTriangle;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author Andrey
 */
public class LinTriangleWrapper implements UnivariateFunction{
    protected LinTriangle elemFunc;

    public LinTriangleWrapper(LinTriangle elemFunc) {
        this.elemFunc = elemFunc;
    }

    @Override
    public double value(double L2) {
        return elemFunc.innerValue(L2);
    }
}
