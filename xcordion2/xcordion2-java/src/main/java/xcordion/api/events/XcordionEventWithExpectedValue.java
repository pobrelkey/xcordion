package xcordion.api.events;

import xcordion.api.IgnoreState;
import xcordion.api.TestElement;

public abstract class XcordionEventWithExpectedValue<T extends TestElement<T>, X> extends XcordionEventWithExpression<T> {
    private final X expected;
    protected XcordionEventWithExpectedValue(T element, IgnoreState ignoreState, String expression, X expected) {
        super(element, ignoreState, expression);
        this.expected = expected;
    }
    public X getExpected() {
        return expected;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        XcordionEventWithExpectedValue<T,X> event = (XcordionEventWithExpectedValue<T,X>) o;
        return (expected != null ? (event.expected != null && expected.equals(event.expected)) : event.expected == null);
    }
}
