package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class MissingRowEvent<T extends TestElement<T>> extends XcordionEvent<T> {
    public MissingRowEvent(T element, IgnoreState ignoreState) {
        super(element, ignoreState);
    }
}
