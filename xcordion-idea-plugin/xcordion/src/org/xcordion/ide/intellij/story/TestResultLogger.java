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

    public String getOutput() {
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
        testPassed = !getOutput().contains("FAILURES!!!");
    }

    public void taskStarted(BuildEvent event) {
    }

    public void taskFinished(BuildEvent event) {
    }

    public String passOrFail() {
        return testPassed ? "PASS" : "FAIL";
    }

}
