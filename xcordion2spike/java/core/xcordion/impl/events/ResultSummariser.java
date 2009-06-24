package xcordion.impl.events;

import xcordion.api.TestElement;
import xcordion.api.XcordionEventListener;
import xcordion.api.IgnoreState;
import xcordion.api.events.XcordionEvent;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.SuccessfulAssertBooleanEvent;
import xcordion.api.events.SuccessfulAssertContainsEvent;
import xcordion.api.events.SuccessfulAssertEqualsEvent;
import xcordion.api.events.FailedAssertBooleanEvent;
import xcordion.api.events.FailedAssertEqualsEvent;
import xcordion.api.events.FailedAssertContainsEvent;
import xcordion.api.events.MissingRowEvent;
import xcordion.api.events.SurplusRowEvent;

public class ResultSummariser<T extends TestElement<T>> implements XcordionEventListener<T> {

    private int successes = 0, failures = 0, exceptions = 0;
    private int ignoredSuccesses = 0, ignoredFailures = 0, ignoredExceptions = 0;
    private boolean expectedToPass = true;
    private Boolean successful;
    private String message;


    public void handleEvent(XcordionEvent<T> xcordionEvent) {
        if (xcordionEvent instanceof ExceptionThrownEvent) {
            markDirty();
            if (xcordionEvent.getIgnoreState() == IgnoreState.NORMATIVE) {
                exceptions++;
            } else {
                ignoredExceptions++;
            }
        } else if (xcordionEvent instanceof SuccessfulAssertBooleanEvent || xcordionEvent instanceof SuccessfulAssertContainsEvent || xcordionEvent instanceof SuccessfulAssertEqualsEvent) {
            markDirty();
            if (xcordionEvent.getIgnoreState() == IgnoreState.NORMATIVE) {
                successes++;
            } else {
                ignoredSuccesses++;
            }
        } else if (xcordionEvent instanceof FailedAssertBooleanEvent || xcordionEvent instanceof FailedAssertEqualsEvent || xcordionEvent instanceof FailedAssertContainsEvent || xcordionEvent instanceof MissingRowEvent || xcordionEvent instanceof SurplusRowEvent) {
            markDirty();
            if (xcordionEvent.getIgnoreState() == IgnoreState.NORMATIVE) {
                failures++;
            } else {
                ignoredFailures++;
            }
        }
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

    public int getIgnoredSuccesses() {
        return ignoredSuccesses;
    }

    public int getIgnoredFailures() {
        return ignoredFailures;
    }

    public int getIgnoredExceptions() {
        return ignoredExceptions;
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
                (exceptions > 0 ? (", Exceptions: " + exceptions) : "") +
                (ignoredSuccesses > 0 ? (", Ignored Successes: " + ignoredSuccesses) : "") +
                (ignoredFailures > 0 ? (", Ignored Failures: " + ignoredFailures) : "") +
                (ignoredExceptions > 0 ? (", Ignored Exceptions: " + ignoredExceptions) : "");
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
