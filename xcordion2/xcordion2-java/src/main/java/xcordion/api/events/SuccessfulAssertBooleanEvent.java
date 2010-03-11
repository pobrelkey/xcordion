package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class SuccessfulAssertBooleanEvent<T extends TestElement<T>> extends XcordionEventWithExpectedValue<T, Boolean> {
    public SuccessfulAssertBooleanEvent(T element, IgnoreState ignoreState, String expression, boolean expected) {
        super(element, ignoreState, expression, expected);
    }
}
