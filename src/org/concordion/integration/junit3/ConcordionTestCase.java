package org.concordion.integration.junit3;

import junit.framework.TestCase;

import org.concordion.Concordion;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;

public abstract class ConcordionTestCase extends TestCase {

    public void testProcessSpecification() throws Throwable {
        Concordion concordion = new ConcordionBuilder().build();
        ResultSummary resultSummary = concordion.process(this);
        System.out.print("Successes: " + resultSummary.getSuccessCount());
        System.out.print(", Failures: " + resultSummary.getFailureCount());
        if (resultSummary.hasExceptions()) {
            System.out.print(", Exceptions: " + resultSummary.getExceptionCount());
        }
        System.out.println("\n");
        resultSummary.assertIsSatisfied();
    }
}
