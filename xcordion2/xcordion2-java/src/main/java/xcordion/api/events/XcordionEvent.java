package xcordion.api.events;

import xcordion.api.IgnoreState;
import xcordion.api.TestElement;

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

    public boolean equals(Object o) {
        XcordionEvent<T> event = (XcordionEvent<T>) o;
        return getClass().equals(o.getClass()) && element.equals(event.element) && ignoreState.equals(event.ignoreState);
    }
}
