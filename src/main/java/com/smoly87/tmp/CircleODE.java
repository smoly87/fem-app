package com.smoly87.tmp;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public  class CircleODE implements FirstOrderDifferentialEquations {

    private double[] c;
    private double omega;

    public CircleODE(double[] c, double omega) {
        this.c     = c;
        this.omega = omega;
    }

    public int getDimension() {
        return 2;
    }

    public void computeDerivatives(double t, double[] y, double[] yDot) {
        yDot[0] = omega * (c[1] - y[1]);
        yDot[1] = omega * (y[0] - c[0]);
    }

}