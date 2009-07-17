package org.xcordion.ide.intellij.story;

import java.util.ArrayList;
import java.util.List;

public class JavaTestRunner {
    private List<TestResultLogger> testResults;

    public JavaTestRunner(List<StoryRunnerActionHandler.TestToRun> tests) {

        testResults = new ArrayList<TestResultLogger>();

        for (StoryRunnerActionHandler.TestToRun test : tests) {
            TestResultLogger buildLogger = new TestResultLogger(test.getName());
            try {
                ModuleAdapter moduleAdapter = new ModuleAdapter(test.getModule());
                Class testClass = moduleAdapter.load(test.getName());
                antJavaTask(moduleAdapter, testClass, buildLogger).execute();
            } catch (ClassNotFoundException e) {
                buildLogger.testNotFound();
            }
            testResults.add(buildLogger);
        }
    }

    private AntJavaTaskRunner antJavaTask(ModuleAdapter moduleAdapter, Class testClass, TestResultLogger buildListener) {
        AntJavaTaskRunner task = new AntJavaTaskRunner(moduleAdapter, testClass);
        task.addBuildListener(buildListener);
        return task;
    }

    public List<TestResultLogger> getTestResults() {
        return testResults;
    }
}
