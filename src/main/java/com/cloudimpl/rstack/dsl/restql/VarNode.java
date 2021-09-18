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
public class VarNode implements RestQLNode {

    private final String var;

    public VarNode(String var) {
        this.var = var;
    }

    public String getVar() {
        return var;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("var", var);
        json.addProperty("type", "varNode");
        return json;
    }
}
