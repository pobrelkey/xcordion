package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class SurplusRowEvent<T extends TestElement<T>> extends XcordionEvent<T> {
    public SurplusRowEvent(T element, IgnoreState ignoreState) {
        super(element, ignoreState);
    }
}
