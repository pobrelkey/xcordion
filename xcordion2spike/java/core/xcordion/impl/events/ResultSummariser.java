package xcordion.impl.events;

import xcordion.api.TestElement;
import xcordion.api.RowNavigator;

public class ResultSummariser<T extends TestElement<T>> extends XcordionEventsAdapter<T> {

    private int successes = 0, failures = 0, exceptions = 0;
    private boolean expectedToPass = true;
    private Boolean successful;
    private String message;


    @Override
    public void exception(T target, String expression, Throwable e) {
        markDirty();
        exceptions++;
    }

    @Override
    public void successfulAssertBoolean(T target, String expression, boolean value) {
        markDirty();
        successes++;
    }

    @Override
    public void failedAssertBoolean(T target, String expression, boolean expected, Object actual) {
        markDirty();
        failures++;
    }

    @Override
    public void successfulAssertEquals(T target, String expression, Object expected) {
        markDirty();
        successes++;
    }

    @Override
    public void failedAssertEquals(T target, String expression, Object expected, Object actual) {
        markDirty();
        failures++;
    }

    @Override
    public void successfulAssertContains(T target, String expression, Object expected, Object actual) {
        markDirty();
        successes++;
    }

    @Override
    public void failedAssertContains(T target, String expression, Object expected, Object actual) {
        markDirty();
        failures++;
    }

    @Override
    public void missingRow(RowNavigator<T> row) {
        markDirty();
        failures++;
    }

    @Override
    public void surplusRow(RowNavigator<T> row) {
        markDirty();
        failures++;
    }

    public boolean isExpectedToPass() {
        return expectedToPass;
    }

    public int getSuccesses() {
        return successes;
    }

    public int getFailures() {
        return failures;
    }

    public int getExceptions() {
        return exceptions;
    }

    public void setExpectedToPass(boolean expectedToPass) {
        this.expectedToPass = expectedToPass;
    }

    public String getMessage() {
        if (message == null) {
            populate();
        }
        return message;
    }

    public boolean isSuccessful() {
        if (successful == null) {
            populate();
        }
        return (boolean) successful;
    }

    public boolean isHappy() {
        return exceptions == 0 && failures == 0;
    }

    public String getScoreLine() {
        return "Successes: " + successes + ", Failures: " + failures +
                (exceptions > 0 ? (", Exceptions: " + exceptions) : "");
    }

    private void markDirty() {
        message = null;
        successful = null;
    }

    private void populate() {
        if (exceptions > 0) {
            message = "Test had exceptions";
            successful = !expectedToPass;
        } else if (failures > 0) {
            message = "Test had failures";
            successful = !expectedToPass;
        } else if (successes == 0) {
            message = "Test was vacuous";
            successful = false;
        } else {
            message = expectedToPass ? "Test succeeded" : "Test succeeded but was expected to fail";
            successful = expectedToPass;
        }
    }
}
