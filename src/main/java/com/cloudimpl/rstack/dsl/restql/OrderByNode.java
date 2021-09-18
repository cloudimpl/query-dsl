/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import com.google.gson.JsonObject;
import java.util.Objects;

/**
 *
 * @author nuwan
 */
public class OrderByNode implements RestQLNode {

    public enum Order {
        ASC, DESC
    }
    private final String fieldName;
    private Order order;

    public OrderByNode(String fieldName, Order order) {
        this.fieldName = fieldName;
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public String getFieldName() {
        return fieldName;
    }

    public OrderByNode setOrder(Order order) {
        this.order = order;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.fieldName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrderByNode other = (OrderByNode) obj;
        if (!Objects.equals(this.fieldName, other.fieldName)) {
            return false;
        }
        return true;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "orderByNode");
        json.addProperty("fieldName", this.fieldName);
        json.addProperty("order", this.order.name());
        return json;
    }
}
