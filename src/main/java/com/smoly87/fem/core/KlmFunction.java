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
@FunctionalInterface
public interface KlmFunction{
    double apply(Element elem, Integer l, Integer m);
}
