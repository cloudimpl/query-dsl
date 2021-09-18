/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.math.BigDecimal;

/**
 *
 * @author nuwan
 */
public interface RestQLNode {

    default <T> T eval(RestQLNode node) {
        return node.eval(this);
    }

    JsonObject toJson();

    public static <T extends RestQLNode> T fromJson(JsonObject json) {
        String type = json.get("type").getAsString();
        switch (type) {
            case "binNode": {
                RestQLNode left = fromJson(json.get("left").getAsJsonObject());
                RestQLNode right = fromJson(json.get("right").getAsJsonObject());
                BinNode binNode = new BinNode(left, BinNode.Op.valueOf(json.get("op").getAsString()), right);
                return (T) binNode;
            }
            case "relNode": {
                String fieldName = json.get("fieldName").getAsString();
                ConstNode constNode = (ConstNode) fromJson(json.get("constNode").getAsJsonObject());
                RelNode relNode = new RelNode(fieldName, RelNode.Op.valueOf(json.get("op").getAsString()), constNode);
                return (T) relNode;
            }
            case "strNode": {
                String val = json.get("val").getAsString();
                return (T) new ConstStringNode(val);
            }
            case "numberNode": {
                BigDecimal val = json.get("val").getAsBigDecimal();
                return (T) new ConstNumberNode(val);
            }
            case "boolNode": {
                boolean val = json.get("val").getAsBoolean();
                return (T) new ConstBooleanNode(val);
            }
            case "boolArrNode": {
                ConstBooleanArrayNode arrNode = new ConstBooleanArrayNode();
                json.get("vals").getAsJsonArray().forEach(el -> arrNode.push(el.getAsBoolean()));
                return (T) arrNode;
            }
            case "numberArrNode": {
                ConstNumberArrayNode arrNode = new ConstNumberArrayNode();
                json.get("vals").getAsJsonArray().forEach(el -> arrNode.push(el.getAsBigDecimal()));
                return (T) arrNode;
            }
            case "strArrNode": {
                ConstStringArrayNode arrNode = new ConstStringArrayNode();
                json.get("vals").getAsJsonArray().forEach(el -> arrNode.push(el.getAsString()));
                return (T) arrNode;
            }
            case "orderByNode": {
                OrderByNode orderBy = new OrderByNode(json.get("fieldName").getAsString(), OrderByNode.Order.valueOf(json.get("order").getAsString()));
                return (T) orderBy;
            }
            case "orderByExpNode": {
                OrderByExpNode orderByExp = new OrderByExpNode();
                JsonArray arr = json.getAsJsonArray("arr");
                arr.forEach(item -> orderByExp.add((OrderByNode) fromJson(item.getAsJsonObject())));
                return (T) orderByExp;
            }
            default: {
                throw new RuntimeException("unknow type: " + json);
            }
        }
    }
}
