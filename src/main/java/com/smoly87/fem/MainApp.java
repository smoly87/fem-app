package com.smoly87.fem;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.smoly87.fem.tasks.HeatEquation2d;
import com.smoly87.fem.tasks.HeatEquationDynamic1d;
import com.smoly87.fem.tasks.PhiEquation1d1;
import com.smoly87.fem.tasks.WaveEquation1d;
import com.smoly87.rendering.Body;
import com.smoly87.tmp.CircleODE;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
       /* PhiEquation1d1 task = new PhiEquation1d1(3);
        System.out.println(task.solve());*/

       Injector injector = Guice.createInjector(new MainModule());
      /* WaveEquation1d task = injector.getInstance(WaveEquation1d.class);
       task.solve(100);*/
        HeatEquation2d task = injector.getInstance(HeatEquation2d.class);
        task.solve();
       /* HeatEquationDynamic1d task = injector.getInstance(HeatEquationDynamic1d.class);
        task.solve(2);*/
       // testCircle();
    }

    protected static void testCircle() {
        FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
        FirstOrderDifferentialEquations ode = new CircleODE(new double[] { 0.0, 0.0 }, 1);
        double[] y = new double[] { 0.0, 1.0 }; // initial state
        //dp853.addStepHandler(createStepHandler());
        dp853.integrate(ode, 0.0, y, 1, y); // now y contains final state at time t=16.0
        System.out.println("ans"+ y[0] + ";" +y[1]);
    }

    protected static void df(double[]d) {
        d[0] = 1;
    }

    protected static StepHandler createStepHandler() {
        StepHandler stepHandler = new StepHandler() {
            @Override
            public void init(double v, double[] doubles, double v1) {

            }

            @Override
            public void handleStep(StepInterpolator stepInterpolator, boolean b) throws MaxCountExceededException {
                double   t = stepInterpolator.getCurrentTime();
                double[] y = stepInterpolator.getInterpolatedState();
                System.out.println(t + ";" + y[0] + ";" +y[1]);

            }
        };
        return stepHandler;
    }
}
