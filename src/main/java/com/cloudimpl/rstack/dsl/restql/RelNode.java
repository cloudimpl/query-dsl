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
public class RelNode implements RestQLNode {

    public enum Op {
        EQ(" = "), GT(" > "), GTE(" >= "), LT(" < "), LTE(" <= "), NE(" <> "), IN(" in ");

        private String op;

        private Op(String op) {
            this.op = op;
        }

        public String getOp() {
            return op;
        }

    }
    private final String fieldName;
    private final Op op;
    private final ConstNode constNode;

    public RelNode(String fieldName, Op op, ConstNode constNode) {
        this.fieldName = fieldName.trim();
        this.op = op;
        this.constNode = constNode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Op getOp() {
        return op;
    }

    public ConstNode getConstNode() {
        return constNode;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "relNode");
        json.addProperty("op", op.name());
        json.addProperty("fieldName", fieldName);
        json.add("constNode", constNode.toJson());
        return json;
    }
}
