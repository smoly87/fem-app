/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core;

/**
 *
 * @author Andrey
 */
public class Vector {
    protected double[] coordinates;

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public Vector(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
