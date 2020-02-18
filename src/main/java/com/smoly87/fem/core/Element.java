/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core;

import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
public class Element {
    protected ArrayList<Integer> nodesList;
    protected ElemFunc elemFunc;

    public void setElemFunc(ElemFunc elemFunc) {
        this.elemFunc = elemFunc;
    }

    public ElemFunc getElemFunc() {
        return elemFunc;
    }

    public ArrayList<Integer> getNodesList() {
        return nodesList;
    }
    protected Mesh mesh;

    public Mesh getMesh() {
        return mesh;
    }

    public double F(double[] phi, double[] xAbs){
        int funcNum = elemFunc.getFuncsCount();
        double v = 0;
        for(int i = 0; i < funcNum; i++){
            double Ni = elemFunc.FA(xAbs, i);
            v += phi[i]*Ni;
        }
        
        return v;
    }
    
    public double getH(){
        double p0 = getPointCoordinates(0)[0];
        double pN = getPointCoordinates(nodesList.size())[0];
        return pN - p0;
    }
    
    protected double[] getPointCoordinates(int pointINd){
        return mesh.getPoints().get(this.getNodesList().get(pointINd)).getCoordinates();
    }
    
    public Element(Mesh mesh, ArrayList<Integer> nodesList) {
        this.nodesList = nodesList;
        this.mesh = mesh;
    }
     
}
