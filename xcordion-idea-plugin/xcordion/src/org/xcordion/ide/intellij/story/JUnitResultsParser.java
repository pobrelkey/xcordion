package org.xcordion.ide.intellij.story;

import java.util.List;

public class JUnitResultsParser {
    private StringBuffer report;

    public JUnitResultsParser(List<TestResultLogger> results) {
        report = new StringBuffer(100);
        report.append("Test Results: ");

        for (TestResultLogger result : results) {
            report.append(TestResultLogger.LINE_SEPARATOR);
            report.append(result.getTestName());
            report.append(": ");
            report.append(result.passOrFail());
            report.append(TestResultLogger.LINE_SEPARATOR);
            report.append(result.getTestOutputPath());
            report.append(TestResultLogger.LINE_SEPARATOR);
        }
    }

    public void printReport() {
        System.out.println(report.toString());
    }
}
