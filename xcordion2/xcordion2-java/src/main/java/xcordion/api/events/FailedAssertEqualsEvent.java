package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class FailedAssertEqualsEvent<T extends TestElement<T>> extends XcordionEventWithExpectedAndActualValues<T, Object> {
    public FailedAssertEqualsEvent(T element, IgnoreState ignoreState, String expression, Object expected, Object actual) {
        super(element, ignoreState, expression, expected, actual);
    }
}
