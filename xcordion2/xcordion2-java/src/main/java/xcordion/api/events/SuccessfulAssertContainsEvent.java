package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class SuccessfulAssertContainsEvent<T extends TestElement<T>> extends XcordionEventWithExpectedAndActualValues<T, Object> {
    public SuccessfulAssertContainsEvent(T element, IgnoreState ignoreState, String expression, Object expected, Object actual) {
        super(element, ignoreState, expression, expected, actual);
    }
}
