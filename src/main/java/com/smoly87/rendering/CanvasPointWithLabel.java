package com.smoly87.rendering;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;

public class CanvasPointWithLabel {
    protected Vector2D position;
    protected Color color;

    public Vector2D getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public CanvasPointWithLabel(Vector2D position, Color color) {
        this.position = position;
        this.color = color;
    }
}
