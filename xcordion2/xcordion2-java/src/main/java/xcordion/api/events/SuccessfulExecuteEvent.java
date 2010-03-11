package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class SuccessfulExecuteEvent<T extends TestElement<T>> extends XcordionEventWithExpression<T> {
    public SuccessfulExecuteEvent(T element, IgnoreState ignoreState, String expression) {
        super(element, ignoreState, expression);
    }
}
