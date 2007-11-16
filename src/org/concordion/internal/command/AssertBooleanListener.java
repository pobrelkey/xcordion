package org.concordion.internal.command;

import java.util.EventListener;

public interface AssertBooleanListener extends EventListener {
    void successReported(AssertBooleanSuccessEvent event);

    void failureReported(AssertBooleanFailureEvent event);
}
