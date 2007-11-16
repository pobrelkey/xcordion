package org.concordion.internal.command;

import org.concordion.api.Element;

public class AssertBooleanFailureEvent {
    private Element element;
    private String expression;
    private boolean expected;

    public AssertBooleanFailureEvent(Element element, String expression, boolean expected) {
        this.element = element;
        this.expression = expression;
        this.expected = expected;
    }

    public Element getElement() {
        return element;
    }

    public String getExpression() {
        return expression;
    }

    public boolean getExpected() {
        return expected;
    }

}
