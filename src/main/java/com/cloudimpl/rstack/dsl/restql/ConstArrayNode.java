/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nuwan
 */
public interface ConstArrayNode extends ConstNode{

    <T> List<T> getVals();

}
