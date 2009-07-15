package org.xcordion.ide.intellij.story;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

class JunitTestListener implements TestListener {
    public void addError(Test test, Throwable throwable) {
        throw new UnsupportedOperationException("Not done yet please come back later");
    }

    public void addFailure(Test test, AssertionFailedError assertionFailedError) {
        throw new UnsupportedOperationException("Not done yet please come back later");
    }

    public void endTest(Test test) {
        throw new UnsupportedOperationException("Not done yet please come back later");
    }

    public void startTest(Test test) {
        throw new UnsupportedOperationException("Not done yet please come back later");
    }
}
