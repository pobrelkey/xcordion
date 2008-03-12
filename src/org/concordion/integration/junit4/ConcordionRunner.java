package org.concordion.integration.junit4;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.notification.RunNotifier;

public class ConcordionRunner extends JUnit4ClassRunner {

    private boolean isMethodInThisClass = false;
    
    public ConcordionRunner(Class<?> fixtureClass) throws InitializationError {
        super(fixtureClass);
    }

    @Override
    protected void validate() {
        // Do nothing
    }
    
    @Override
    protected List<Method> getTestMethods() {
        List<Method> methods = new ArrayList<Method>(super.getTestMethods());
        try {
            methods.add(getClass().getDeclaredMethod("processSpecification"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise ConcordionRunner", e);
        }
        return methods;
    }
    
    @Override
    protected void invokeTestMethod(Method method, RunNotifier notifier) {
        isMethodInThisClass = method.getDeclaringClass().equals(getClass());
        super.invokeTestMethod(method, notifier);
    }
    
    @Override
    protected Object createTest() throws Exception {
        return isMethodInThisClass ? this : super.createTest();
    }
    
    public void processSpecification() throws Throwable {
        ResultSummary resultSummary = new ConcordionBuilder().build().process(super.createTest());
        resultSummary.print(System.out);
        resultSummary.assertIsSatisfied();
    }
}
