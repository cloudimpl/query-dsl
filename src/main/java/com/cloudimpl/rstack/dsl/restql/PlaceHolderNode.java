package com.cloudimpl.rstack.dsl.restql;

import com.google.gson.JsonObject;

public class PlaceHolderNode implements ConstNode{
    private final String val;

    public PlaceHolderNode(String val) {
        this.val = val.trim();
    }

    public String getVal() {
        return val;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("val", val);
        json.addProperty("type", "placeHolderNode");
        return json;
    }
}
