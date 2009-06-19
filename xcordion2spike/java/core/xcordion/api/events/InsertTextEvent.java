package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class InsertTextEvent<T extends TestElement<T>> extends XcordionEventWithExpression<T> {
    private final Object result;
    public InsertTextEvent(T element, IgnoreState ignoreState, String expression, Object result) {
        super(element, ignoreState, expression);
        this.result = result;
    }
    public Object getResult() {
        return result;
    }
}
