package xcordion.api;

import xcordion.api.events.XcordionEvent;

public interface XcordionEventListener<T extends TestElement<T>>  {
    void handleEvent(XcordionEvent<T> xcordionEvent);
}
