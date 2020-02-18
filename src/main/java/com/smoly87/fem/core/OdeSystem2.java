package com.smoly87.fem.core;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.ode.SecondOrderDifferentialEquations;

public class OdeSystem2 implements SecondOrderDifferentialEquations {
    private RealMatrix A;
    private RealMatrix FM;
    public OdeSystem2(RealMatrix A, RealVector F) {
        this.A = A;
        this.FM = new Array2DRowRealMatrix(F.toArray());
    }

    @Override
    public int getDimension() {
        return A.getRowDimension();
    }

    @Override
    public void computeSecondDerivatives(double t, double[] y, double[] yDot, double[] yDDot) {
        RealMatrix yMatr = new Array2DRowRealMatrix(y);
        yMatr.setColumn(0,y);
        RealMatrix M = A.multiply(yMatr).subtract(FM).scalarMultiply(Math.sin(t));
        yDDot = M.getColumn(0);
    }
}
