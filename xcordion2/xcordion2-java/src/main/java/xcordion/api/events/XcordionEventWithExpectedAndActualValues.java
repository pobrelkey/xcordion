package xcordion.api.events;

import xcordion.api.IgnoreState;
import xcordion.api.TestElement;

public abstract class XcordionEventWithExpectedAndActualValues<T extends TestElement<T>, X> extends XcordionEventWithExpectedValue<T, X> {
    private final Object actual;
    protected XcordionEventWithExpectedAndActualValues(T element, IgnoreState ignoreState, String expression, X expected, Object actual) {
        super(element, ignoreState, expression, expected);
        this.actual = actual;
    }
    public Object getActual() {
        return actual;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        XcordionEventWithExpectedAndActualValues<T,X> event = (XcordionEventWithExpectedAndActualValues<T,X>) o;
        return (actual != null ? (event.actual != null && actual.equals(event.actual)) : event.actual == null);
    }
}
