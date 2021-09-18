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
public class FieldCheckNode implements RestQLNode {

    private final String fieldName;
    private final boolean checkExist;

    public FieldCheckNode(String fieldName, boolean checkExist) {
        this.fieldName = fieldName;
        this.checkExist = checkExist;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isCheckExist() {
        return checkExist;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("fieldName", fieldName);
        json.addProperty("checkExist", checkExist);
        json.addProperty("type", "checkFieldNode");
        return json;
    }
}
