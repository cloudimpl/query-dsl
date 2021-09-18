/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import com.google.gson.JsonObject;

/**
 *
 * @author nuwan
 */
public class ConstStringNode implements ConstNode{

    private final String val;

    public ConstStringNode(String val) {
        this.val = val.trim();
    }

    public String getVal() {
        return val;
    }
    
     @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("val", val);
        json.addProperty("type", "strNode");
        return json;
    }
}
