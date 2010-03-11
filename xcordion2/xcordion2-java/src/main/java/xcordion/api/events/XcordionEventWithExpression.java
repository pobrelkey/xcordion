package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public abstract class XcordionEventWithExpression<T extends TestElement<T>> extends XcordionEvent<T> {
    private final String expression;
    public XcordionEventWithExpression(T element, IgnoreState ignoreState, String expression) {
        super(element, ignoreState);
        this.expression = expression;
    }
    public String getExpression() {
        return expression;
    }
}
