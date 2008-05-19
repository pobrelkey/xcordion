package org.concordion.internal.command;

import org.concordion.api.Element;

public class AssertBooleanSuccessEvent {
    private Element element;

    public AssertBooleanSuccessEvent(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}
