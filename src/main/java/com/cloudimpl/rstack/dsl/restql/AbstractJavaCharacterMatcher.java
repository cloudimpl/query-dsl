/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudimpl.rstack.dsl.restql;

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CustomMatcher;
/**
 *
 * @author nuwan
 */


public abstract class AbstractJavaCharacterMatcher extends CustomMatcher {

    protected AbstractJavaCharacterMatcher(String label) {
        super(label);
    }

    @Override
    public final boolean isSingleCharMatcher() {
        return true;
    }

    @Override
    public final boolean canMatchEmpty() {
        return false;
    }

    @Override
    public boolean isStarterChar(char c) {
        return acceptChar(c);
    }

    @Override
    public final char getStarterChar() {
        return 'a';
    }

    public final <V> boolean match(MatcherContext<V> context) {
        if (!acceptChar(context.getCurrentChar())) {
            return false;
        }
        context.advanceIndex(1);
        context.createNode();
        return true;
    }

    protected abstract boolean acceptChar(char c);
}