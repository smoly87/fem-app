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
public class Mesh {
    protected ArrayList<Vector> points;
    protected ArrayList<Element> elements;
    protected ArrayList<Integer> convHullInd;

    public ArrayList<Integer> getConvHullInd() {
        return convHullInd;
    }

    public void setConvHullInd(ArrayList<Integer> convHullInd) {
        this.convHullInd = convHullInd;
    }

   
    /**
     * This method finds elements which contains x in its own range
     * @param x
     * @return 
     */
    public int findElement(double x){
        int i = 0;
        for(Element elem: elements){
            double elemStart = elem.getPointCoordinates(0)[0] ;
            if(x > elemStart){
                return i;
            }
            i++;
        }
        return -1;
    }

    public ArrayList<Vector> getPoints() {
        return points;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public int getNodesCount(){
        return points.size();
    }
    
    public Mesh(ArrayList<Vector> points) {
        this.points = points;
        this.elements = new ArrayList<>();

    }
    
    public void applyElemFunc(ElemFuncBuilder funcBuilder){
        for(Element elem : elements){
            elem.setElemFunc(funcBuilder.build(elem));
        }
    }
    
    public void addElement(Element elem){
        elements.add(elem);
    }
}
