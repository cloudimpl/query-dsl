/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import com.cloudimpl.rstack.dsl.restql.BinNode;
import com.cloudimpl.rstack.dsl.restql.ConstArrayNode;
import com.cloudimpl.rstack.dsl.restql.ConstBooleanNode;
import com.cloudimpl.rstack.dsl.restql.ConstNumberNode;
import com.cloudimpl.rstack.dsl.restql.ConstStringNode;
import com.cloudimpl.rstack.dsl.restql.RelNode;
import com.cloudimpl.rstack.dsl.restql.RestQLNode;
import com.google.gson.JsonObject;

/**
 *
 * @author nuwan
 */
public class SqlNode implements RestQLNode {

    @Override
    public String eval(RestQLNode node) {
        if (node instanceof ConstArrayNode) {
            return ConstArrayNode.class.cast(node).getVals().toString();
        } else if (node instanceof ConstStringNode) {
            return String.valueOf(ConstStringNode.class.cast(node).getVal());
        } else if (node instanceof ConstNumberNode) {
            return String.valueOf(ConstNumberNode.class.cast(node).getVal());
        } else if (node instanceof ConstBooleanNode) {
            return String.valueOf(ConstBooleanNode.class.cast(node).getVal());
        } else if (node instanceof RelNode) {
            RelNode rel = RelNode.class.cast(node);
            return rel.getFieldName() + rel.getOp().getOp() + (String) rel.getConstNode().eval(this);
        } else if (node instanceof BinNode) {
            BinNode binNode = BinNode.class.cast(node);
            return "(" + binNode.getLeft().eval(this) + binNode.getOp().getOp() + binNode.getRight().eval(this) + ")";
        }
        throw new RuntimeException("unknown node :" + node.getClass().getName());
    }

    @Override
    public JsonObject toJson() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
