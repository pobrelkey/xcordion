package test.concordion.internal;

import org.concordion.internal.OgnlValidatingEvaluator;

import junit.framework.TestCase;

public class ExpressionTest extends TestCase {

    public void testWhitespaceIsIgnoredInEvaluationExpressions() throws Exception {
        assertValidEvaluationExpression("myMethod(#var1,   #var2)");
        assertValidEvaluationExpression("  #var   =    myMethod   (    #var1,   #var2    )   ");
        assertValidEvaluationExpression("#var=myMethod(#var1,#var2)");
    }

    public void testWhitespaceIsIgnoredInSetVariableExpressions() throws Exception {
        assertValidSetVariableExpression("  #var   =    myMethod   (    #var1,   #var2    )   ");
        assertValidSetVariableExpression("  #var   =    myProp");
        assertValidSetVariableExpression("#var=myProp");
        assertValidSetVariableExpression("#var=myMethod(#var1,#var2)");
    }

    private void assertValidEvaluationExpression(String expression) {
        try {
            OgnlValidatingEvaluator.validateEvaluationExpression(expression);
        } catch (Exception e) {
            fail("Expression incorrectly declared invalid: " + expression);
        }
    }

    private void assertValidSetVariableExpression(String expression) {
        try {
            OgnlValidatingEvaluator.validateSetVariableExpression(expression);
        } catch (Exception e) {
            fail("Expression incorrectly declared invalid: " + expression);
        }
    }
}
