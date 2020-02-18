/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core.elemfunc.d2;

import com.smoly87.fem.core.ElemFunc;
import com.smoly87.fem.core.ElemFuncBuilder;
import com.smoly87.fem.core.Element;


/**
 *
 * @author Andrey
 */
public class LinUniformTriangleBuilder implements ElemFuncBuilder {

    @Override
    public ElemFunc build(Element elem) {
        return new LinUniformTriangle(elem);
    }
    
}
