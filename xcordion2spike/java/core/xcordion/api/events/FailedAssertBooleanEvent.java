package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class FailedAssertBooleanEvent<T extends TestElement<T>> extends XcordionEventWithExpectedAndActualValues<T, Boolean> {
    public FailedAssertBooleanEvent(T element, IgnoreState ignoreState, String expression, boolean expected, Object actual) {
        super(element, ignoreState, expression, expected, actual);
    }
}
