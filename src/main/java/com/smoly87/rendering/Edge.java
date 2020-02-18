package com.smoly87.rendering;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Edge {
    private Vector2D v1;
    private Vector2D v2;
    private Vector2D n;

    public Edge(Vector2D v1, Vector2D v2) {
        this.v1 = v1;
        this.v2 = v2;
        n = MatrixUtils.createFrom3D(MatrixUtils.createFrom2D(v2.subtract(v1)).crossProduct(new Vector3D(0d,0d,1.0))) ;
        n = n.normalize();
    }

    public Vector2D getN() {
        return n;
    }

    public Vector2D getV1() {
        return v1;
    }

    public Vector2D getV2() {
        return v2;
    }

    public Vector2D getMiddle() {
        return new Vector2D(
                (getV1().getX() + getV2().getX()) / 2,
                (getV1().getY() + getV2().getY()) / 2);
    }
}
