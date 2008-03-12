package org.concordion.api;

import java.io.PrintStream;

public interface ResultSummary {

    void assertIsSatisfied();

    boolean hasExceptions();

    boolean hasFailures();

    long getSuccessCount();
    
    long getFailureCount();

    long getExceptionCount();

    void print(PrintStream out);
}
