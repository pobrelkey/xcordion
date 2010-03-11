package xcordion.api.events;

import xcordion.api.IgnoreState;
import xcordion.api.TestElement;

public abstract class XcordionEventWithExpression<T extends TestElement<T>> extends XcordionEvent<T> {
    private final String expression;
    public XcordionEventWithExpression(T element, IgnoreState ignoreState, String expression) {
        super(element, ignoreState);
        this.expression = expression;
    }
    public String getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        XcordionEventWithExpression<T> event = (XcordionEventWithExpression<T>) o;
        return (expression != null ? (event.expression != null && expression.equals(event.expression)) : event.expression == null);
    }
}
