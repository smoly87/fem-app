package com.smoly87.rendering;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class Body {
    public Vector3D getPosition() {
        return position;
    }

    public List<Edge> getEdgesList() {
        return edgesList;
    }

    public List<Body> getSubBodies() {
        return subBodies;
    }

    public void setSubBodies(List<Body> subBodies) {
        this.subBodies = subBodies;
    }

    public Body(List<Vector2D> vertexes) {
        this.vertexes = vertexes;
    }

    public Body() {
        forces = new ArrayList<>();
        v = new Vector3D(0d,0d,0d);
        position = new Vector3D(0d,0d,0d);
        subBodies = new ArrayList<>();
        edgesList = new ArrayList<>();
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    protected List<Body> subBodies;
    protected Vector3D position; // coords of center and theta
    protected List<Vector2D> vertexes;
    protected double invI = 0.1; // moment of inertia
    protected double mInv = 0.1; // mass
    protected Vector3D v; // velocity x,y,w (linear and angular)
    protected List<Force> forces;
    protected List<Edge> edgesList;
    protected  String title;

    public RealMatrix getInverseM() {
        return new Array2DRowRealMatrix(new double[][] {
                {mInv,0,0},
                {0, mInv,0},
                {0,0, invI}
        });
    }

    public Vector2D getAbsCoord(Vector2D relCoord) {
        return  getCenter()
                .add(
                        new Vector2D(getTransformationMatrix()
                                .multiply(new Array2DRowRealMatrix(relCoord.toArray()))
                                .getColumn(0))
                );
    }

    public Vector2D getCenter() {
        return new Vector2D(position.getX(), position.getY());
    }


    public RealMatrix getTransformationMatrix() {
        double theta = position.getZ() ;
        return new Array2DRowRealMatrix(new double[][]{
                {cos(theta), -sin(theta)},
                {sin(theta), cos(theta)},
        });
    }

    public List<Vector2D> getVertexes() {
        return vertexes;
    }

    public List<Vector2D> getVertexesAbs() {
        return vertexes.stream().map(this::getAbsCoord).collect(Collectors.toList());
    }

    public void setVertexes(List<Vector2D> vertexes) {
        this.vertexes = vertexes;
        int N = vertexes.size();
        if (N != 2) {
            for(int i = 0; i < N - 1; i++) {
                edgesList.add(new Edge(vertexes.get(i), vertexes.get(i+1)));
            }
            //edgesList.add(new Edge(vertexes.get(N - 1), vertexes.get(0)));
        } else {
            edgesList.add(new Edge(vertexes.get(0), vertexes.get(1)));
        }


    }

    public List<Edge> getEdges() {
        return edgesList;
    }

    public List<Edge> getEdgesAbs() {
        return edgesList.stream()
                .map(edge -> new Edge(getAbsCoord(edge.getV1()), getAbsCoord(edge.getV2())))
                .collect(Collectors.toList());
    }

    public double getInvI() {
        return invI;
    }

    public void setInvI(double invI) {
        this.invI = invI;
    }

    public double getInvM() {
        return mInv;
    }

    public void setInvM(double mInv) {
        this.mInv = mInv;
    }

    public Vector3D getV() {
        return v;
    }

    public void setV(Vector3D v) {
        this.v = v;
    }

    public List<Force> getForces() {
        return forces;
    }

    public void setForces(List<Force> forces) {
        this.forces = forces;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
