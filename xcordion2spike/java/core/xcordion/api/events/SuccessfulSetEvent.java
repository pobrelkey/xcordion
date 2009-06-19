package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class SuccessfulSetEvent<T extends TestElement<T>> extends XcordionEventWithExpression<T> {
    private final Object value;
    public SuccessfulSetEvent(T element, IgnoreState ignoreState, String expression, Object value) {
        super(element, ignoreState, expression);
        this.value = value;
    }
    public Object getValue() {
        return value;
    }
}
