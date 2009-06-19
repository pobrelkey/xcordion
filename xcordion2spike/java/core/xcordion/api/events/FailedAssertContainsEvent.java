package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class FailedAssertContainsEvent<T extends TestElement<T>> extends XcordionEventWithExpectedAndActualValues<T, Object> {
    public FailedAssertContainsEvent(T element, IgnoreState ignoreState, String expression, Object expected, Object actual) {
        super(element, ignoreState, expression, expected, actual);
    }
}
