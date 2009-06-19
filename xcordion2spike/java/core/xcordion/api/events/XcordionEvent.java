package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public abstract class XcordionEvent<T extends TestElement<T>> {
    protected final T element;
    protected final IgnoreState ignoreState;

    protected XcordionEvent(T element, IgnoreState ignoreState) {
        this.element = element;
        this.ignoreState = ignoreState;
    }

    public T getElement() {
        return element;
    }

    public IgnoreState getIgnoreState() {
        return ignoreState;
    }
}
