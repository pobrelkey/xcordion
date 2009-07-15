package org.xcordion.ide.intellij.story;

import junit.framework.Test;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;

class JunitBuildLogger implements BuildListener {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final StringBuffer log = new StringBuffer();
    private final StringBuffer allLevelLog = new StringBuffer();
    private JUnitAdapter jUnitAdapter;

    public JunitBuildLogger(Class testClass) {
        jUnitAdapter = new JUnitAdapter(testClass);
    }

    public void messageLogged(BuildEvent event) {
        if (event.getPriority() == Project.MSG_INFO) {
            log.append(event.getMessage() + LINE_SEPARATOR);
        }
        allLevelLog.append(event.getMessage() + LINE_SEPARATOR);
    }

    public Test toTestCase() {
//        try {
//            return new TextTestRunnerOutputTestCase(junitAdapter, log.toString());
//        } catch (TestRunnerError error) {
//            LogThrowable throwable = new LogThrowable(error.getMessage(), allLevelLog.toString());
//            return junitAdapter.toTestCase(throwable);
//        }
        return null;
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

    public String getRunnerClassName() {
        return jUnitAdapter.runnerClass().getName();
    }

    public String getResults() {
        return log.toString();
    }
}
