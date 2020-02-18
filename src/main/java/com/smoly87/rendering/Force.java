package com.smoly87.rendering;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Force {
    protected Vector2D applyPoint;
    protected Vector2D force;

    public Vector2D getApplyPoint() {
        return applyPoint;
    }

    public Vector2D getForce() {
        return force;
    }

    public Force(Vector2D applyPoint, Vector2D force) {
        this.applyPoint = applyPoint;
        this.force = force;
    }
}
