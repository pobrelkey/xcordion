package xcordion.api.events;

import xcordion.api.TestElement;
import xcordion.api.IgnoreState;

public class ChangedIgnoreStateEvent<T extends TestElement<T>> extends XcordionEvent<T> {
    public ChangedIgnoreStateEvent(T element, IgnoreState ignoreState) {
        super(element, ignoreState);
    }
}
