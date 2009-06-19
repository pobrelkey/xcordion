package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public abstract class XcordionEventWithExpectedValue<T extends TestElement<T>, X> extends XcordionEventWithExpression<T> {
    private final X expected;
    protected XcordionEventWithExpectedValue(T element, IgnoreState ignoreState, String expression, X expected) {
        super(element, ignoreState, expression);
        this.expected = expected;
    }
    public X getExpected() {
        return expected;
    }
}
