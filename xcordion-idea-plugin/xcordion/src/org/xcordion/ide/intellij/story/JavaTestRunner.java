package org.xcordion.ide.intellij.story;

import java.util.ArrayList;
import java.util.List;

public class JavaTestRunner {
    private List<JunitBuildLogger> results;

    public JavaTestRunner(List<StoryRunnerActionHandler.TestToRun> tests) {

        results = new ArrayList<JunitBuildLogger>();

        for (StoryRunnerActionHandler.TestToRun test : tests) {
            try {
                ModuleAdapter moduleAdapter = new ModuleAdapter(test.getModule());
                Class testClass = moduleAdapter.load(test.getName());
                JunitBuildLogger buildLogger = new JunitBuildLogger(testClass);
                antJavaTask(moduleAdapter, test.getName(), buildLogger).execute();

                results.add(buildLogger);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private AntJavaTaskRunner antJavaTask(ModuleAdapter moduleAdapter, String testClassName, JunitBuildLogger buildListener) {
        AntJavaTaskRunner task = new AntJavaTaskRunner(moduleAdapter, testClassName, buildListener.getRunnerClassName());
        task.addBuildListener(buildListener);
        return task;
    }

    public List<JunitBuildLogger> getResults() {
        return results;
    }
}
