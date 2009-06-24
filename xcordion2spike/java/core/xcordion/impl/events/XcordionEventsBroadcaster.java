package xcordion.impl.events;

import xcordion.api.XcordionEventListener;
import xcordion.api.TestElement;
import xcordion.api.events.XcordionEvent;

import java.util.ArrayList;

public class XcordionEventsBroadcaster<T extends TestElement<T>> implements XcordionEventListener<T> {

    private ArrayList<XcordionEventListener<T>> listeners = new ArrayList<XcordionEventListener<T>>();

    public void addListener(XcordionEventListener<T> listener) {
        listeners.add(listener);
    }

    public void handleEvent(XcordionEvent<T> xcordionEvent) {
        for (XcordionEventListener<T> listener : listeners) {
            listener.handleEvent(xcordionEvent);
        }
    }
}
