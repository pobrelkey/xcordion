package xcordion.impl.events;

import xcordion.api.XcordionEvents;
import xcordion.api.TestElement;
import xcordion.api.RowNavigator;

abstract public class XcordionEventsAdapter<T extends TestElement<T>> implements XcordionEvents<T> {
    public void begin(T target) {
    }

    public void succesfulSet(T target, String expression, Object value) {
    }

    public void exception(T target, String expression, Throwable e) {
    }

    public void succesfulExecute(T target, String expression) {
    }

    public void insertText(T target, String expression, Object result) {
    }

    public void successfulAssertBoolean(T target, String expression, boolean value) {
    }

    public void failedAssertBoolean(T target, String expression, boolean expected, Object actual) {
    }

    public void successfulAssertEquals(T target, String expression, Object expected) {
    }

    public void failedAssertEquals(T target, String expression, Object expected, Object actual) {
    }

    public void successfulAssertContains(T target, String expression, Object expected, Object actual) {
    }

    public void failedAssertContains(T target, String expression, Object expected, Object actual) {
    }

    public void missingRow(RowNavigator<T> row) {
    }

    public void surplusRow(RowNavigator<T> row) {
    }

    public void end(T target) {
    }            
}
