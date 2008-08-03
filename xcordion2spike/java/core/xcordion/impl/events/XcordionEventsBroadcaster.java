package xcordion.impl.events;

import xcordion.api.XcordionEvents;
import xcordion.api.TestElement;
import xcordion.api.RowNavigator;

import java.util.ArrayList;

public class XcordionEventsBroadcaster<T extends TestElement<T>> implements XcordionEvents<T> {

    private ArrayList<XcordionEvents<T>> listeners = new ArrayList<XcordionEvents<T>>();

    public void addListener(XcordionEvents<T> listener) {
        listeners.add(listener);
    }

    public void begin(T target) {
        for (XcordionEvents<T> listener : listeners) {
            listener.begin(target);
        }
    }

    public void succesfulSet(T target, String expression, Object value) {
        for (XcordionEvents<T> listener : listeners) {
            listener.succesfulSet(target, expression, value);
        }
    }

    public void exception(T target, String expression, Throwable e) {
        for (XcordionEvents<T> listener : listeners) {
            listener.exception(target, expression, e);
        }
    }

    public void succesfulExecute(T target, String expression) {
        for (XcordionEvents<T> listener : listeners) {
            listener.succesfulExecute(target, expression);
        }
    }

    public void insertText(T target, String expression, Object result) {
        for (XcordionEvents<T> listener : listeners) {
            listener.insertText(target, expression, result);
        }
    }

    public void successfulAssertBoolean(T target, String expression, boolean value) {
        for (XcordionEvents<T> listener : listeners) {
            listener.successfulAssertBoolean(target, expression, value);
        }
    }

    public void failedAssertBoolean(T target, String expression, boolean expected, Object actual) {
        for (XcordionEvents<T> listener : listeners) {
            listener.failedAssertBoolean(target, expression, expected, actual);
        }
    }

    public void successfulAssertEquals(T target, String expression, Object expected) {
        for (XcordionEvents<T> listener : listeners) {
            listener.successfulAssertEquals(target, expression, expected);
        }
    }

    public void failedAssertEquals(T target, String expression, Object expected, Object actual) {
        for (XcordionEvents<T> listener : listeners) {
            listener.failedAssertEquals(target, expression, expected, actual);
        }
    }

    public void successfulAssertContains(T target, String expression, Object expected, Object actual) {
        for (XcordionEvents<T> listener : listeners) {
            listener.successfulAssertContains(target, expression, expected, actual);
        }
    }

    public void failedAssertContains(T target, String expression, Object expected, Object actual) {
        for (XcordionEvents<T> listener : listeners) {
            listener.failedAssertContains(target, expression, expected, actual);
        }
    }

    public void missingRow(RowNavigator<T> row) {
        for (XcordionEvents<T> listener : listeners) {
            listener.missingRow(row);
        }
    }

    public void surplusRow(RowNavigator<T> row) {
        for (XcordionEvents<T> listener : listeners) {
            listener.surplusRow(row);
        }
    }

    public void end(T target) {
        for (XcordionEvents<T> listener : listeners) {
            listener.end(target);
        }
    }
}
