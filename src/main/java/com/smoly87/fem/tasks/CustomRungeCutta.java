package com.smoly87.fem.tasks;

import com.smoly87.fem.core.OdeSystem;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class CustomRungeCutta {
    public void setStepHandler(BiConsumer<Double, Double[]> stepHandler) {
        this.stepHandler = stepHandler;
    }

    protected BiConsumer<Double, Double[]> stepHandler;
    public void integrate(OdeSystem odeSys, double[] Y0, double dtStep, double tMin, double tMax) {
        int N = Y0.length;
        double t = tMin;
        double[] Y = new double[Y0.length];
        Y = Y0;
        while (t < tMax) {
            double[] yDer = new double[Y0.length];
            double[] k1 = calcK(odeSys, t, Y, 0,new double[N],dtStep);
            double[] k2 = calcK(odeSys, t, Y, dtStep/2,scalarMultiply(k1, 0.5),dtStep);
            double[] k3 = calcK(odeSys, t, Y, dtStep/2,scalarMultiply(k2, 0.5),dtStep);
            double[] k4 = calcK(odeSys, t, Y, dtStep, k3, dtStep);
            t += dtStep;

            for(int i = 0; i < N; i++) {
                Y[i] = Y[i] + (1.0/6.0) *(k1[i] + 2*k2[i] + 2*k3[i] + k4[i]);
                raiseStepChange(t, Y);
            }
        }
    }


    protected void raiseStepChange(double t, double[] Y) {
        int N = Y.length;
      Double[] YD = new Double[N];
      for(int i = 0; i < N; i++) {
          YD[i] = Y[i];
      }
      stepHandler.accept(t, YD);
    }

    protected double[] scalarMultiply(double[] v, double k) {
        return Arrays.stream(v).map(val -> val * k).toArray();
    }

    protected double[] calcK(OdeSystem odeSys, double t, double[] Y, double dt, double dy[], double dtStep) {
        int N = Y.length;
        double[] k = new double[N];
        double[] yDer = new double[N];

        for(int i = 0; i < N; i++) {
            Y[i] = Y[i] + dy[i];
        }

        yDer = odeSys.computeDerivativesR(t + dt, Y);

        for(int i = 0; i < N; i++) {
            k[i] = dtStep * yDer[i];
        }
        return k;
    }
}
