/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core;

import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author Andrey
 */
@FunctionalInterface
public interface SysBlockBuilder{
    RealMatrix apply(Element elem, Integer l, Integer m);
}
