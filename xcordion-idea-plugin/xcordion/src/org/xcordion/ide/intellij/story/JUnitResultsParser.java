package org.xcordion.ide.intellij.story;

import java.util.List;

public class JUnitResultsParser {
    private StringBuffer report;

    public JUnitResultsParser(List<JunitBuildLogger> results) {
        report = new StringBuffer(100);
        report.append("Test Results: ");

        for (JunitBuildLogger result : results) {
            report.append(JunitBuildLogger.LINE_SEPARATOR);
            report.append(result.getTestClass().getSimpleName());
            report.append(": ");
            report.append(passOrFail(result.getResults()));
            report.append(JunitBuildLogger.LINE_SEPARATOR);
            report.append(result.getTestOutputPath());
            report.append(JunitBuildLogger.LINE_SEPARATOR);
        }
    }

    private String passOrFail(String results) {
        if (results.contains("FAILURES!!!")) {
            return "FAIL";
        }
        return "PASS";
    }

    public void printReport() {
        System.out.println(report.toString());
    }
}
