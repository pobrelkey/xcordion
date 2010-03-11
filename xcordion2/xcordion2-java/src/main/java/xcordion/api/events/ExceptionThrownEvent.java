package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class ExceptionThrownEvent<T extends TestElement<T>> extends XcordionEventWithExpression<T> {
    private final Throwable throwable;
    public ExceptionThrownEvent(T element, IgnoreState ignoreState, String expression, Throwable throwable) {
        super(element, ignoreState, expression);
        this.throwable = throwable;
    }
    public Throwable getThrowable() {
        return throwable;
    }
}
