package org.xcordion.ide.intellij.story;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;

class TestResultLogger implements BuildListener {
    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final StringBuffer log = new StringBuffer();
    private final StringBuffer allLevelLog = new StringBuffer();
    private String testOutputPath;
    private boolean outputPathLogged = false;
    private boolean testPassed;
    private final String testName;
    private TestOutcome testOutcome;
    private boolean isExpectedToPass = true;

    public TestResultLogger(String testName) {
        this.testName = testName;
    }

    public void messageLogged(BuildEvent event) {
        String eventMessage = event.getMessage();
        if (event.getPriority() == Project.MSG_INFO) {
            if (!outputPathLogged && eventMessage.contains(StoryPageResults.getJavaTmpDirectory())) {
                testOutputPath = eventMessage.substring(eventMessage.indexOf(StoryPageResults.getJavaTmpDirectory()));
                outputPathLogged = true;
            }
            log.append(eventMessage + LINE_SEPARATOR);
        }
        allLevelLog.append(eventMessage + LINE_SEPARATOR);
    }

    public String getTestSimpleName(){
        return testName.substring(testName.lastIndexOf("."));
    }

    public String getTestName() {
        return testName;
    }

    private String getOutput() {
        return log.toString();
    }

    public String getTestOutputPath() {
        return testOutputPath;
    }

    public void buildStarted(BuildEvent event) {
    }

    public void buildFinished(BuildEvent event) {
    }

    public void targetStarted(BuildEvent event) {        
    }

    public void targetFinished(BuildEvent event) {
        if(getOutput().contains("FAILURES!!!")) {
            testOutcome = isExpectedToPass ? TestOutcome.FAIL : TestOutcome.FAIL_EXPECTEDTOFAIL;
        } else {
            testOutcome = isExpectedToPass ? TestOutcome.PASS : TestOutcome.PASS_EXPECTEDTOFAIL;
        }

    }

    public void taskStarted(BuildEvent event) {
    }

    public void taskFinished(BuildEvent event) {
    }

    public TestOutcome outcome() {
        return testOutcome;
    }

    public void testNotFound() {
        testOutcome = TestOutcome.NOT_FOUND;        
    }

    public boolean passed() {
        return testOutcome == TestOutcome.PASS;
    }

    public void setIsExpectedToPass(boolean expectedToPass) {
        isExpectedToPass = expectedToPass;
    }

    enum TestOutcome {
        PASS("PASS", "#9F9"), FAIL("FAIL", "#F99"), NOT_FOUND("NOT FOUND", "#FE9"), FAIL_EXPECTEDTOFAIL("FAIL (expected to fail)", "#FCC"), PASS_EXPECTEDTOFAIL("PASS (expected to fail)", "#CFC");
        private final String text;
        private final String style;

        TestOutcome(String text, String style) {
            this.text = text;
            this.style = style;
        }

        public String text() {
            return text;
        }

        public String htmlStyle() {
            return "style=\"background-color: "+style+"; font-weight: bold\"";
        }
    }
}
