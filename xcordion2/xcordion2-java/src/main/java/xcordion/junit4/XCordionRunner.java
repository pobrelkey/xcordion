package xcordion.junit4;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import xcordion.lang.java.SimpleXcordionRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class XCordionRunner extends BlockJUnit4ClassRunner {

    static private Method MAGIC_METHOD_REFERENCE;

    public XCordionRunner(Class<?> testClass) throws org.junit.runners.model.InitializationError {
        super(testClass);
    }

	protected List<FrameworkMethod> computeTestMethods() {
        ArrayList<FrameworkMethod> result = new ArrayList<FrameworkMethod>(super.computeTestMethods());
        result.add(new FrameworkMethod(xcordionSpecification()));
        return result;
	}

 	protected Statement methodInvoker(FrameworkMethod frameworkMethod, Object test) {
         Method method = frameworkMethod.getMethod();
         if (method == xcordionSpecification()) {
             return new XcordionSpecificationStatement(test);
         } else {
             return super.methodInvoker(frameworkMethod, test);
         }
 	}

    // used to generate a magic Method reference to stand in for running the actual test specification (see MAGIC_METHOD field)
    private Method xcordionSpecification() {
        if (MAGIC_METHOD_REFERENCE == null) {
            try {
                MAGIC_METHOD_REFERENCE = XCordionRunner.class.getDeclaredMethod("xcordionSpecification");
            } catch (NoSuchMethodException e) {
                // WTF? this method most certainly exists...
                e.printStackTrace();
            }
        }
        return MAGIC_METHOD_REFERENCE;
    }


    private class XcordionSpecificationStatement extends Statement {
        private final Object testInstance;

        public XcordionSpecificationStatement(Object testInstance) {
            this.testInstance = testInstance;
        }

        public void evaluate() throws Throwable {
            new SimpleXcordionRunner(testInstance).runTest();
        }
    }
}
