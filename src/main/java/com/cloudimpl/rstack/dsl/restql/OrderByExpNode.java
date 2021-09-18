/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author nuwan
 */
public class OrderByExpNode implements RestQLNode {

    private final List<OrderByNode> orderByList = new LinkedList<>();

    public OrderByExpNode() {
    }
    
    public OrderByExpNode add(OrderByNode orderBy)
    {
        this.orderByList.add(orderBy);
        return this;
    }

    public List<OrderByNode> getOrderByList() {
        return orderByList;
    }

    
    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray arr = new JsonArray();
        orderByList.forEach(i->arr.add(i.toJson()));
        json.addProperty("type", "orderByExpNode");
        json.add("arr", arr);
        return json;
    }

}
