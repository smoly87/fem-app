/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smoly87.fem.core.elemfunc.d2.lin;

import com.smoly87.fem.core.ElemFunc;
import com.smoly87.fem.core.ElemFuncBuilder;
import com.smoly87.fem.core.Element;
import com.smoly87.fem.core.elemfunc.d2.uniform.LinUniformTriangle;


/**
 *
 * @author Andrey
 */
public class LinTriangleBuilder implements ElemFuncBuilder {

    @Override
    public ElemFunc build(Element elem) {
        return new LinTriangle(elem);
    }
    
}
