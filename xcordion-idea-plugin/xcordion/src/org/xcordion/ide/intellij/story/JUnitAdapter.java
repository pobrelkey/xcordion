package org.xcordion.ide.intellij.story;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.junit.internal.runners.TestClass;
import org.junit.runner.JUnitCore;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JUnitAdapter {
    private final Class runnerClass;
    private final Method[] testMethods;
    private final Class testClass;

    public JUnitAdapter(Class aClass) {
        testClass = aClass;
        boolean isJUnit4 = !isSubClassOfJUnit3TestCase();
        if (isJUnit4) {
            runnerClass = JUnitCore.class;
            testMethods = getJUnit4TestMethods();
        } else {
            runnerClass = TestRunner.class;
            testMethods = getMethodsStartWithTest();
        }
    }

    private boolean isSubClassOfJUnit3TestCase() {
        return getClassFromClassLoaderOfTestClass(TestCase.class).isAssignableFrom(testClass);
    }

    private Method[] getMethodsStartWithTest() {
        Method[] methods = testClass.getMethods();
        List<Method> testMethods = new ArrayList();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().startsWith("test")) {
                testMethods.add(methods[i]);
            }
        }
        return toArray(testMethods);
    }

    private Method[] getJUnit4TestMethods() {
        return toArray(new TestClass(testClass).getAnnotatedMethods(getClassFromClassLoaderOfTestClass(org.junit.Test.class)));
    }

    private Class getClassFromClassLoaderOfTestClass(Class target) {
        try {
            return testClass.getClassLoader().loadClass(target.getName());
        } catch (ClassNotFoundException e) {
            return target;
        }
    }

    private Method[] toArray(List<Method> testMethods) {
        return testMethods.toArray(new Method[testMethods.size()]);
    }

    public Class runnerClass() {
        return runnerClass;
    }
}

