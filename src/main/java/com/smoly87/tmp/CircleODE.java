package com.smoly87.tmp;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public  class CircleODE implements FirstOrderDifferentialEquations {

    private double[] c;
    private double omega;
    RealMatrix A;
    public CircleODE(double[] c, double omega) {
        this.c     = c;
        this.omega = omega;
        A = new Array2DRowRealMatrix(new double[][]{
                {0,-1},
                {1, 0}
        });
    }

    public int getDimension() {
        return 2;
    }

    public void computeDerivatives(double t, double[] y, double[] yDot) {
        yDot[0] = omega * (c[1] - y[1]);
        yDot[1] = omega * (y[0] - c[0]);

        RealMatrix yMatr = new Array2DRowRealMatrix(y);
        RealMatrix M = A.multiply(yMatr).scalarMultiply(omega);
        double[] yDot1 = M.getColumn(0);
        String s = yMatr.toString();
        System.arraycopy(yDot1, 0, yDot, 0, yDot.length);
    }

}