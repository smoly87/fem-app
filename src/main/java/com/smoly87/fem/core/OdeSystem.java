package com.smoly87.fem.core;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class OdeSystem implements FirstOrderDifferentialEquations {
    private RealMatrix A;
    private RealMatrix FM;
    public OdeSystem(RealMatrix A, RealVector F) {
        this.A = A;
        this.FM = new Array2DRowRealMatrix(F.toArray());
    }

    @Override
    public int getDimension() {
        return A.getRowDimension();
    }

    @Override
    public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
        RealMatrix yMatr = new Array2DRowRealMatrix(y);
        yMatr.setColumn(0,y);
        RealMatrix M = A.multiply(yMatr).subtract(FM);
        yDot = M.getColumn(0);
        double ts  =2;
    }
    public double[] computeDerivativesR(double t, double[] y) throws MaxCountExceededException, DimensionMismatchException {
        RealMatrix yMatr = new Array2DRowRealMatrix(y);
        yMatr.setColumn(0,y);
        RealMatrix M = A.multiply(yMatr).subtract(FM);
        return M.getColumn(0);
    }

}
