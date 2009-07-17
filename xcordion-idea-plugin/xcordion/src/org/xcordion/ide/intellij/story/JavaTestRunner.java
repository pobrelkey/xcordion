package org.xcordion.ide.intellij.story;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;

import java.util.ArrayList;
import java.util.List;

public class JavaTestRunner {
    private List<TestResultLogger> testResults;

    public JavaTestRunner(List<StoryRunnerActionHandler.TestToRun> tests) {

        testResults = new ArrayList<TestResultLogger>();
        ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        indicator.setIndeterminate(false);
        indicator.setFraction(0);
        indicator.setText("Running "+ tests.size() +" tests ...");

        double numberOfTestsRan = 0;
        for (StoryRunnerActionHandler.TestToRun test : tests) {
            TestResultLogger buildLogger = new TestResultLogger(test.getName());
            indicator.setText2(test.getName());

            try {
                ModuleAdapter moduleAdapter = new ModuleAdapter(test.getModule());
                Class testClass = moduleAdapter.load(test.getName());
                antJavaTask(moduleAdapter, testClass, buildLogger).execute();
            } catch (ClassNotFoundException e) {
                buildLogger.testNotFound();
            }
            
            indicator.setFraction(++numberOfTestsRan / tests.size());
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
