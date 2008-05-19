package org.concordion.integration.junit4;

import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;
import org.junit.internal.runners.MethodRoadie;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class FixtureMethodRoadie extends MethodRoadie {

    private Object fTest;

    public FixtureMethodRoadie(Object test, TestMethod method, RunNotifier notifier, Description description) {
        super(test, method, notifier, description);
        fTest = test;
    }

    @Override
    protected void runTestMethod() {
        try {
            ResultSummary resultSummary = new ConcordionBuilder().build().process(fTest);
            resultSummary.print(System.out);
            resultSummary.assertIsSatisfied();
        } catch (Throwable e) {
            addFailure(e);
        }
    }
}
