package xcordion.junit3;

import junit.framework.TestCase;

import java.util.List;
import java.util.Collections;

import xcordion.lang.java.SimpleXcordionRunner;

public class XcordionTestCase extends TestCase {
    private static final String PROPERTY_RUN_EXPECTED_TO_FAIL_TESTS = "xcordion.runExpectedToFailTests";


    protected List<String> getNullKeywords() {
        return Collections.EMPTY_LIST;
    }

    protected boolean isExpectedToPass() {
        return true;
    }

    public void testProcessDocument() throws Throwable {
        boolean shouldRunExpectedToFail = Boolean.parseBoolean(System.getProperty(PROPERTY_RUN_EXPECTED_TO_FAIL_TESTS, "true"));
        Class<? extends XcordionTestCase> testClass = this.getClass();
        if (!shouldRunExpectedToFail && !isExpectedToPass()) {
            System.out.println("SKIPPING expected-to-fail test: " + testClass.getName());
            return;
        }

        new SimpleXcordionRunner(testClass.newInstance()).runTest(isExpectedToPass());
    }


}

