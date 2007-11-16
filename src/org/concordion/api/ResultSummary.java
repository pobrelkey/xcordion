package org.concordion.api;

public interface ResultSummary {

    void assertIsSatisfied();

    boolean hasExceptions();

    long getSuccessCount();
    
    long getFailureCount();

    long getExceptionCount();
}
