package com.smoly87.fem.tasks.tension2d;

import com.smoly87.fem.core.blockmatrix.BlockMatrixStiffnessMatrixBuilder;
import com.smoly87.fem.core.blockmatrix.SystemBlockMatrix;
import com.smoly87.fem.core.boundaryconditions.BoundaryConditions;
import com.smoly87.fem.core.boundaryconditions.BoundaryConditionsBuilder;
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

    protected void fillMatrixes() {
        int N = mesh.getNodesCount();
        K = fillK();
        C = fillC();
        boundaryConditions = this.getBoundaryConditions();

        F = boundaryConditions.applyBoundaryConditionsToRightPart(K, F);
        K = boundaryConditions.applyBoundaryConditionsToLeftPart(K);
        C = boundaryConditions.applyBoundaryConditionsToLeftPart(C);
    }

    protected RealMatrix fillK() {
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
        RealMatrix K = BlockMatrixStiffnessMatrixBuilder.fillGlobalStiffness(mesh, SymbK);
        return K;
    }

    protected RealMatrix fillC() {
        SystemBlockMatrix Nm = new SystemBlockMatrix(new ElemFuncType[][]{
                {ElemFuncType.dFdx, ElemFuncType.I},
                {ElemFuncType.I, ElemFuncType.dFdy},
        }, new double[][]{
                {1, 0},
                {0, 1},
        });
        RealMatrix C = BlockMatrixStiffnessMatrixBuilder.fillGlobalStiffness(mesh, Nm);
        return C;
    }

    @Override
    public BoundaryConditions getBoundaryConditions() {
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
        return new ArrayRealVector(Y0);
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
        double[] Y0 = getInitialConditions(mesh.getPoints(), DIM_COUNT).toArray(); // The also cut boundary conditions
        solveTimeProblemSecondOrder(createStepHandler(), Y0 ,0, Tmax, 0.01 );
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

    protected double[] convertToCoordinatesOnlySolution(double[] y) {
        int N = y.length / 2;
        double[] res = new double[N];
        System.arraycopy(y, N, res, 0, N);
        return res;
    }

    protected void visualizeStepFromSolution(double t, double[] y) {
        y = convertToCoordinatesOnlySolution(y);
        List<Body> bodies = new ArrayList<>();
        int N = y.length / 2;
        int i = 0;
        for(Element element: mesh.getElements()) {
            double[] finalY = y;
            List<Vector2D> curElemVertexes = element.getNodesList().stream()
                    .map(pointInd -> {
                        double[] coords = mesh.getPoints().get(pointInd).getCoordinates();
                        coords[0] = coords[0] + finalY[pointInd * 2]; // x offset
                        coords[1] = coords[1] + finalY[pointInd * 2]; // y offset
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
