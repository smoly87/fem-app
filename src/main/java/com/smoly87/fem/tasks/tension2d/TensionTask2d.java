package com.smoly87.fem.tasks.tension2d;

import com.google.common.collect.Streams;
import com.smoly87.fem.core.blockmatrix.BlockMatrixStiffnessMatrixBuilder;
import com.smoly87.fem.core.blockmatrix.SystemBlockMatrix;
import com.smoly87.fem.core.boundaryconditions.BoundaryConditions;
import com.smoly87.fem.core.boundaryconditions.BoundaryConditionsBuilder;
import com.smoly87.fem.core.boundaryconditions.BoundaryConditionsOld;
import com.smoly87.fem.core.*;
import com.smoly87.fem.core.elemfunc.d2.lin.LinTriangleBuilder;
import com.smoly87.meshloader.MeshLoaderGmsh;
import com.smoly87.rendering.Body;
import com.smoly87.rendering.SceneRender;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class TensionTask2d extends Task {
    protected final int DIM_COUNT = 2; // (ux, uy) shifts by x and y
    protected final double INITIAL_OFFSET = 0.001;
    protected final double delta = 0.001;


    protected RealMatrix C;
    protected SceneRender sceneRender;

    @Inject
    public TensionTask2d(SceneRender sceneRender) {
        this.sceneRender = sceneRender;
    }

    protected void fillMatrixes(){
        final double v = 0.1;
        final double E = 0.1;
        RealMatrix Dkoofs = new Array2DRowRealMatrix(new double[][]{
                {1, v, 0},
                {v, 1, 0},
                {0, 0, (1 - v) / 2}

        }).scalarMultiply(1 / (1 - v*v));

        SystemBlockMatrix D = new SystemBlockMatrix(Dkoofs.getData());
        SystemBlockMatrix B = new SystemBlockMatrix(new ElemFuncType[][]{
                {ElemFuncType.dFdx, ElemFuncType.I},
                {ElemFuncType.I, ElemFuncType.dFdy},
                {ElemFuncType.dFdy, ElemFuncType.dFdx},
        }, new double[][]{
                {1, 0},
                {0, 1},
                {1, 1},
        });
        // Forces should be set separately
        SystemBlockMatrix SymbK = B.multiply(D).multiply(B);
        K = BlockMatrixStiffnessMatrixBuilder.fillGlobalStiffness(mesh, SymbK);

        int N = mesh.getNodesCount();
        C = new Array2DRowRealMatrix(N, N);
        C = fillGlobalStiffness(C, this::Clm); // TODO: How C looks like ? Probably Nl * Nm But what about dimension ?
    }

    protected BoundaryConditions getBoundaryConditions() {
        int boundaryPointsCount = (int)mesh.getPoints()
                .stream().filter(vector -> Math.abs(vector.getCoordinates()[0]) < delta)
                .count();

        int N = mesh.getPoints().size();
        ArrayList<Integer> boundaryPointsInd = new ArrayList<>();
        for(int i = 0; i < N; i++) {
            double[] coords = mesh.getPoints().get(i).getCoordinates();
            if (Math.abs(coords[0]) < delta) {
                boundaryPointsInd.add(i);
            }
        }
        BoundaryConditionsBuilder boundaryConditionsBuilder = BoundaryConditions.builder(DIM_COUNT);
        return boundaryConditionsBuilder
                .setPointIndexes(boundaryPointsInd)
                .addValues(0, new double[boundaryPointsCount])
                .addValues(1, new double[boundaryPointsCount])
                .build();
    }

    // F ?

    protected RealVector getInitialConditions(ArrayList<Vector> points, int blockSize){
        int N = mesh.getPoints().size();
        double[] Y0 = new double[N];
        for(int i = 0; i < N; i++) {
            double[] coords = mesh.getPoints().get(i).getCoordinates();
            if (coords[0] + delta > 1.0)  {
                int ind = i * blockSize; // We want to set up only x offset
                Y0[ind] = INITIAL_OFFSET;
            }
        }
    }

    protected void init() {

        MeshLoaderGmsh gmshLoader = new MeshLoaderGmsh(false);
        mesh = gmshLoader.loadMesh("assets/test.msh");
        //mesh.applyElemFunc(new LinUniformTriangleBuilder());
        mesh.applyElemFunc(new LinTriangleBuilder());

        this.initMatrixes(mesh.getNodesCount());
        fillMatrixes();
        //applySpatialBoundaryConditions();
    }

    public void  solve(int elemNum){
        final double Tmax = 10d;
        init();

        RealMatrix CInv = MatrixUtils.inverse(C);
        K = CInv.multiply(K).scalarMultiply(-1); // df/dx = A*x - form of ode sys       //
        RealMatrix FM = new Array2DRowRealMatrix(F.toArray());
        F = new ArrayRealVector(CInv.multiply(FM).getColumn(0)) ;
        double[] Y0; // The also cut boundary conditions
        solveTimeProblemSecondOrder(createStepHandler(), getInitialConditions(mesh.getPoints()).toArray(),0, Tmax, 0.01 );
    }

    protected StepHandler createStepHandler() {
        StepHandler stepHandler = new StepHandler() {
            @Override
            public void init(double v, double[] doubles, double v1) {

            }

            @Override
            public void handleStep(StepInterpolator stepInterpolator, boolean b) throws MaxCountExceededException {
                double   t = stepInterpolator.getCurrentTime();
                double[] y = stepInterpolator.getInterpolatedState();
                visualizeStepFromSolution(t, y);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        return stepHandler;
    }

    protected void visualizeStepFromSolution(double t, double[] y) {
        // TODO: restore boundaries and removes speed information, after that it will be (u1, v1, u2, v2,...)
        List<Body> bodies = new ArrayList<>();
        int N = y.length / 2;
        int i = 0;
        for(Element element: mesh.getElements()) {
            List<Vector2D> curElemVertexes = element.getNodesList().stream()
                    .map(pointInd -> {
                        double[] coords = mesh.getPoints().get(pointInd).getCoordinates();
                        coords[0] = coords[0] + y[pointInd * 2]; // x offset
                        coords[1] = coords[1] + y[pointInd * 2]; // y offset
                        return new Vector2D(coords);
                    })
                    .collect(Collectors.toList());
            Body curElemBody = new Body();
            curElemBody.setVertexes(curElemVertexes);
            bodies.add(curElemBody);
            i++;
        }
        sceneRender.clear();
        sceneRender.renderBodies(bodies, Color.BLUE);
        sceneRender.redraw();
    }
}
