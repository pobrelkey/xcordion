package org.xcordion.ide.intellij.story;

import com.intellij.openapi.module.Module;

import java.util.List;

public class JavaTestRunner {
    public JavaTestRunner(Module module, List<String> testClassNames) {
        ModuleAdapter moduleAdapter = new ModuleAdapter(module);

        for (String testClassName : testClassNames) {
            try {
                Class testClass = moduleAdapter.load(testClassName);
                JunitBuildLogger buildLogger = new JunitBuildLogger(testClass);
                antJavaTask(moduleAdapter, testClassName, buildLogger).execute();

                System.out.println("Results:  " + buildLogger.getResults());

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
}
