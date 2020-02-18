/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core;

import com.smoly87.fem.core.elemfunc.d1.Element1d;
import com.smoly87.fem.core.elemfunc.d1.LinNBuilder;
import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
public class SimpleMeshBuilder {
    /**
     *
     * @param elemNum
     * @param L Length of the line
     * @param applyFunc
     * @return
     */
    public static Mesh create1dLineMesh(int elemNum, double L,  boolean applyFunc){
       // Mesh Mesh = new Mesh();
       int N = elemNum + 1;
       ArrayList<Vector> points = new ArrayList<>(N);
       ArrayList<Element> elements = new ArrayList<>(elemNum);
               
       double h = L/(double)(N-1);
       for(int i = 0; i < N; i++){
           points.add(new Vector(new double[]{h*i}));
       }
       
       Mesh mesh = new Mesh(points);
       
       for(int i = 0; i < elemNum; i++){
           ArrayList<Integer> nodesList = new ArrayList<>(2);
           
           nodesList.add(i);
           nodesList.add(i + 1);
           
           Element el = new Element1d(mesh, nodesList);
           mesh.addElement(el);
       }
       
        if(applyFunc) mesh.applyElemFunc(new LinNBuilder());
       //mesh.setNodesCount(N);
       
       return mesh;
    }
    
    public static Mesh create1dLineMeshQuad(int elemNum, boolean applyFunc){
       // Mesh Mesh = new Mesh();
       int N = 3*elemNum  - (elemNum -1);
       ArrayList<Vector> points = new ArrayList<>(N);
               
       double h = 1/(double)(N-1);
       for(int i = 0; i < N; i++){
           points.add(new Vector(new double[]{h*i}));
       }
       
       Mesh mesh = new Mesh(points);
       
       for(int i = 0; i < elemNum; i++){
           ArrayList<Integer> nodesList = new ArrayList<>(3);
           int s = 0; //Minus common points number according to current element number
           if(i > 0) s = 3*i-(i);
           nodesList.add(s);
           nodesList.add(s + 1);
           nodesList.add(s + 2);
           
           Element el = new Element1d(mesh, nodesList);
           mesh.addElement(el);
       }
       
      
       //mesh.setNodesCount(N);
       if(applyFunc) mesh.applyElemFunc(new LinNBuilder());
       return mesh;
    }
}
