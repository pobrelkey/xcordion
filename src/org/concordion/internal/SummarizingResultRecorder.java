package org.concordion.internal;

import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.ResultSummary;

import java.util.ArrayList;
import java.util.List;

public class SummarizingResultRecorder implements ResultRecorder, ResultSummary {

    private List<Result> recordedResults = new ArrayList<Result>();

    public void record(Result result) {
        recordedResults.add(result);
    }

    public void assertIsSatisfied() {
        if (hasFailures()) {
            throw new AssertionError("Specification has failure(s). See output HTML for details.");
        }
        if (hasExceptions()) {
            throw new AssertionError("Specification has exception(s). See output HTML for details.");
        }
    }

    public boolean hasExceptions() {
        return getExceptionCount() > 0;
    }

    public boolean hasFailures() {
        return getFailureCount() > 0;
    }

    public long getCount(Result result) {
        int count = 0;
        for (Result candidate : recordedResults) {
            if (candidate == result) {
                count++;
            }
        }
        return count;
    }

    public long getExceptionCount() {
        return getCount(Result.EXCEPTION);
    }

    public long getFailureCount() {
        return getCount(Result.FAILURE);
    }

    public long getSuccessCount() {
        return getCount(Result.SUCCESS);
    }

}
