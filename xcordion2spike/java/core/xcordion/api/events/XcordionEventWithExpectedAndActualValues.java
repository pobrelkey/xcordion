package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public abstract class XcordionEventWithExpectedAndActualValues<T extends TestElement<T>, X> extends XcordionEventWithExpectedValue<T, X> {
    private final Object actual;
    protected XcordionEventWithExpectedAndActualValues(T element, IgnoreState ignoreState, String expression, X expected, Object actual) {
        super(element, ignoreState, expression, expected);
        this.actual = actual;
    }
    public Object getActual() {
        return actual;
    }
}
