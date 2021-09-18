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
public class BinNode implements RestQLNode {

    public enum Op {
        AND(" and "), OR(" or ");

        private String op;

        private Op(String op) {
            this.op = op;
        }

        public String getOp() {
            return op;
        }

    };

    private final RestQLNode left;
    private final Op op;
    private final RestQLNode right;

    public BinNode(RestQLNode left, Op op, RestQLNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public RestQLNode getLeft() {
        return left;
    }

    public Op getOp() {
        return op;
    }

    public RestQLNode getRight() {
        return right;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type","binNode");
        json.addProperty("op", op.name());
        json.add("left", left.toJson());
        json.add("right", right.toJson());
        return json;
    }
}
