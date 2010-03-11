package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class EndEvent<T extends TestElement<T>> extends XcordionEvent<T> {
    public EndEvent(T element, IgnoreState ignoreState) {
        super(element, ignoreState);
    }
}
