package spec.concordion.command.badCommand;

import org.concordion.integration.junit3.ConcordionTestCase;
import test.concordion.TestRig;
import test.concordion.ExceptionResult;

public class AzrtEqlzTest extends ConcordionTestCase {
    public ExceptionResult exceptionExpected(String fragment) {
        return new TestRig().processExceptionThrowingFragment(fragment);
    }
}
