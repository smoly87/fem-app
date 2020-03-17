package com.smoly87.fem.tasks;

import com.google.inject.Inject;
import com.smoly87.fem.core.ElemFuncType;
import com.smoly87.fem.core.Element;
import com.smoly87.fem.core.Task;
import com.smoly87.fem.core.elemfunc.d2.lin.LinTriangleBuilder;
import com.smoly87.fem.core.elemfunc.d2.uniform.LinUniformTriangleBuilder;
import com.smoly87.meshloader.MeshLoaderGmsh;
import com.smoly87.rendering.Body;
import com.smoly87.rendering.SceneRender;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HeatEquation2d extends Task {
    protected SceneRender sceneRender;

    @Inject
    public HeatEquation2d(SceneRender sceneRender) {
        this.sceneRender = sceneRender;
    }

    protected RealMatrix C;

    protected double Clm(Element elem, Integer l, Integer m){
        return elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.F, l, m);
    }

    protected double Klm(Element elem, Integer l, Integer m){
        return elem.getElemFunc().integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, l, m) +
                elem.getElemFunc().integrate(ElemFuncType.dFdy, ElemFuncType.dFdy, l, m);
    }

    protected void fillMatrixes(){
        K = fillGlobalStiffness(K, this::Klm);
        int N = mesh.getNodesCount();
        C = new Array2DRowRealMatrix(N, N);
        C = fillGlobalStiffness(C, this::Clm);
    }

    protected void init() {

        MeshLoaderGmsh gmshLoader = new MeshLoaderGmsh(false);
        mesh = gmshLoader.loadMesh("assets/test1.msh");
        //mesh.applyElemFunc(new LinUniformTriangleBuilder());
        mesh.applyElemFunc(new LinTriangleBuilder());

        this.initMatrixes(mesh.getNodesCount());
        fillMatrixes();
        //applySpatialBoundaryConditions();
        drawMesh();
    }

    protected void drawMesh() {
        List<Body> bodies = new ArrayList<>();
        int i = 0;
        for(Element element: mesh.getElements()) {
            List<Vector2D> curElemVertexes = element.getNodesList().stream()
                    .map(pointInd -> new Vector2D(mesh.getPoints().get(pointInd).getCoordinates()))
                    .collect(Collectors.toList());
            Body curElemBody = new Body();
            curElemBody.setVertexes(curElemVertexes);
            bodies.add(curElemBody);
            //if(i >10) break;
            i++;
        }
        sceneRender.clear();
        sceneRender.renderBodies(bodies, Color.BLUE);
        sceneRender.redraw();
    }

    public double[][] solve(){
        init();
        return new double[1][1];
    }
}
