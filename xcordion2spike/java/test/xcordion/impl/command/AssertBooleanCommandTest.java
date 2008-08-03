package xcordion.impl.command;

import org.jmock.Expectations;
import xcordion.api.EvaluationContext;

public class AssertBooleanCommandTest extends AbstractCommandTest {

    public void testExpectTrueIsFalse() {
        assertSimpleSadPath(true);
    }

    public void testExpectFalseIsTrue() {
        assertSimpleSadPath(false);
    }

    public void testExpectTrueIsTrue() {
        assertSimpleHappyPath(true);
    }

    public void testExpectFalseIsFalse() {
        assertSimpleHappyPath(false);
    }

    private void assertSimpleSadPath(final boolean expected) {
        AssertBooleanCommand command = new AssertBooleanCommand(expected);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(!expected));

            one(broadcaster).failedAssertBoolean(emptyElement, expression, expected, !expected);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    private void assertSimpleHappyPath(final boolean expected) {
        AssertBooleanCommand command = new AssertBooleanCommand(expected);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(expected));

            one(broadcaster).successfulAssertBoolean(emptyElement, expression, expected);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testEvalResultIsNotABoolean() {
        AssertBooleanCommand command = new AssertBooleanCommand(true);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(42));

            one(broadcaster).failedAssertBoolean(emptyElement, expression, true, 42);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testEvalResultIsNull() {
        AssertBooleanCommand command = new AssertBooleanCommand(true);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(null));

            one(broadcaster).failedAssertBoolean(emptyElement, expression, true, null);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testEvalBlowsUp() {
        AssertBooleanCommand command = new AssertBooleanCommand(true);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final Throwable error = new Error("this is only a test");

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(throwException(error));

            one(broadcaster).exception(emptyElement, expression, error);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

}
