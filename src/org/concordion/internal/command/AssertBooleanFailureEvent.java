package org.concordion.internal.command;

import org.concordion.api.Element;

public class AssertBooleanFailureEvent {
    private Element element;
    private String expression;
    private Object actual;

    public AssertBooleanFailureEvent(Element element, String expression, Object actual) {
        this.element = element;
        this.expression = expression;
        this.actual = actual;
    }

    public Element getElement() {
        return element;
    }

    public String getExpression() {
        return expression;
    }

    public Object getActual() {
        return actual;
    }
}
