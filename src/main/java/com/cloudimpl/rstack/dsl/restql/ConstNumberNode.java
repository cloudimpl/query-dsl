/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import com.google.gson.JsonObject;
import java.math.BigDecimal;

/**
 *
 * @author nuwan
 */
public class ConstNumberNode implements ConstNode{

    private final BigDecimal val;

    public ConstNumberNode(BigDecimal val) {
        this.val = val;
    }

    public BigDecimal getVal() {
        return val;
    }
    
     @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("val", val);
        json.addProperty("type", "numberNode");
        return json;
    }
}
