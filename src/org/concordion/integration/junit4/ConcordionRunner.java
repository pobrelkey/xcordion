package org.concordion.integration.junit4;

import java.lang.reflect.InvocationTargetException;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.MethodValidator;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class ConcordionRunner extends JUnit4ClassRunner {

    private Description fixtureDescription;

    public ConcordionRunner(Class<?> fixtureClass) throws InitializationError {
        super(fixtureClass);
        fixtureDescription = Description.createTestDescription(fixtureClass, "[Concordion Specification]");
    }

    @Override
    public Description getDescription() {
        Description spec = super.getDescription();
        spec.addChild(fixtureDescription);
        return spec;
    }
    
    @Override
    protected void validate() throws InitializationError {
        MethodValidator methodValidator = new MethodValidator(getTestClass());
        methodValidator.validateNoArgConstructor();
        methodValidator.validateStaticMethods();
        methodValidator.assertValid();
    }

    @Override
    protected void runMethods(final RunNotifier notifier) {
        super.runMethods(notifier);
        Class<?> javaClass = getTestClass().getJavaClass();
        Description description = fixtureDescription;
        TestMethod method;
        Object test;
        try {
            method = new TestMethod(javaClass.getMethod("getClass"), getTestClass());
            test = createTest();
        } catch (InvocationTargetException e) {
            notifier.testAborted(description, e.getCause());
            return;         
        } catch (Exception e) {
            notifier.testAborted(description, e);
            return;
        }
        new FixtureMethodRoadie(test, method, notifier, description).run();
    }
}
