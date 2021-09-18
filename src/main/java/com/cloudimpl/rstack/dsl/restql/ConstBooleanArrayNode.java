/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nuwan
 */
public class ConstBooleanArrayNode implements ConstArrayNode {

    private final List<Boolean> vals;

    public ConstBooleanArrayNode() {
        this.vals = new LinkedList<>();
    }

    public ConstArrayNode push(Boolean val) {
        this.vals.add(val);
        return this;
    }

    @Override
    public List<Boolean> getVals() {
        return this.vals;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray arr = new JsonArray();
        vals.forEach(b -> arr.add(b));
        json.add("vals", arr);
        json.addProperty("type", "boolArrNode");
        return json;
    }

}
