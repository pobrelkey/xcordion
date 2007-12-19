package org.concordion.api;

public interface ResultSummary {

    void assertIsSatisfied();

    boolean hasExceptions();

    boolean hasFailures();

    long getSuccessCount();
    
    long getFailureCount();

    long getExceptionCount();
}
