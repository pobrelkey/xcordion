package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class SuccessfulAssertEqualsEvent<T extends TestElement<T>> extends XcordionEventWithExpectedValue<T, Object> {
    public SuccessfulAssertEqualsEvent(T element, IgnoreState ignoreState, String expression, Object expected) {
        super(element, ignoreState, expression, expected);
    }
}
