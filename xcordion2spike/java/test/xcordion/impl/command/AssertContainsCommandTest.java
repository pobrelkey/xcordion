package xcordion.impl.command;

import xcordion.api.EvaluationContext;
import org.jmock.Expectations;

public class AssertContainsCommandTest extends AbstractCommandTest {

    public void testSimpleHappyPath() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "tally";
        final String actual = "totally mexico";

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(actual));

            one(evalContext).getValue(emptyElement, String.class);
            will(returnValue(expected));

            one(broadcaster).successfulAssertContains(emptyElement, expression, expected, actual);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testSimpleSadPath() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "tally";
        final String actual = "awesome welles";

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(actual));

            one(evalContext).getValue(emptyElement, String.class);
            will(returnValue(expected));

            one(broadcaster).failedAssertContains(emptyElement, expression, expected, actual);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testEvalReturnsNull() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "tally";
        final String actual = null;

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(actual));

            one(evalContext).getValue(emptyElement, String.class);
            will(returnValue(expected));

            one(broadcaster).failedAssertContains(emptyElement, expression, expected, actual);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testEvalBlowsUp() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "tally";
        final Throwable error = new Error("this is only a test");

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(throwException(error));

            one(broadcaster).exception(emptyElement, expression, error);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testExpectedIsNull() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = null;
        final String actual = "today ridicule, tomorrow really cool";

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(actual));

            one(evalContext).getValue(emptyElement, String.class);
            will(returnValue(expected));

            one(broadcaster).successfulAssertContains(emptyElement, expression, expected, actual);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testNonStringOperands() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final Object expected = 1337;
        final Object actual = 31337357;

        context.checking(new Expectations(){{
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(actual));

            one(evalContext).getValue(emptyElement, String.class);
            will(returnValue(expected));

            one(broadcaster).successfulAssertContains(emptyElement, expression, expected, actual);
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }
}
