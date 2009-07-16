package org.xcordion.ide.intellij.story;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;

class JunitBuildLogger implements BuildListener {
    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final StringBuffer log = new StringBuffer();
    private final StringBuffer allLevelLog = new StringBuffer();
    private JUnitAdapter jUnitAdapter;
    private String testOutputPath;
    private boolean outputPathLogged = false;

    public JunitBuildLogger(Class testClass) {
        jUnitAdapter = new JUnitAdapter(testClass);
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

    public Class getTestClass() {
        return jUnitAdapter.getTestClass();
    }

    public String getRunnerClassName() {
        return jUnitAdapter.runnerClass().getName();
    }

    public String getResults() {
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
    }

    public void taskStarted(BuildEvent event) {
    }

    public void taskFinished(BuildEvent event) {
    }
}
