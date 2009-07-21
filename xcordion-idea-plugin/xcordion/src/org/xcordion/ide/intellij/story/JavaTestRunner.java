package org.xcordion.ide.intellij.story;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JavaTestRunner {
    private List<TestResultLogger> testResults;

    public JavaTestRunner(List<TestToRun> tests) {

        testResults = new ArrayList<TestResultLogger>();
        ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        indicator.setIndeterminate(false);
        indicator.setFraction(0);

        double numberOfTestsRan = 1;
        for (TestToRun test : tests) {
            ProgressManager.getInstance().checkCanceled(); // terminate task if cancel button on progress bar is pressed
            indicator.setText("Running " + (int) numberOfTestsRan + " of " + tests.size() +" tests ...");
            indicator.setText2(test.getHtmlName());
            indicator.setFraction(numberOfTestsRan / tests.size());

            TestResultLogger buildLogger = new TestResultLogger(test.getHtmlName());

            if (test.getModule() == null) {
                buildLogger.testNotFound();
            } else {
                try {
                    ModuleAdapter moduleAdapter = new ModuleAdapter(test.getModule());
                    Class testClass = moduleAdapter.load(test.getFullyQualifiedClassName());
                    buildLogger.setIsExpectedToPass(isExpectedToPass(testClass));
                    antJavaTask(moduleAdapter, testClass, buildLogger).execute();
                } catch (ClassNotFoundException e) {
                    buildLogger.testNotFound();
                }
            }
            numberOfTestsRan++;
            testResults.add(buildLogger);
        }
    }

    private boolean isExpectedToPass(Class testClass) {
        try {
            Method method = testClass.getMethod("isExpectedToPass");
            return (Boolean) method.invoke(testClass.newInstance());
        } catch (Exception e) {
            // blithely ignore all errors
            return true;
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
