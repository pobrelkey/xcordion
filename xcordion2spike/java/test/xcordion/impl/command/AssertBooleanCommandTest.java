package xcordion.impl.command;

import org.jmock.Expectations;
import xcordion.api.EvaluationContext;
import xcordion.api.IgnoreState;
import xcordion.api.events.FailedAssertBooleanEvent;
import xcordion.api.events.SuccessfulAssertBooleanEvent;
import xcordion.api.events.ExceptionThrownEvent;

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
            allowing(evalContext).getIgnoreState();
            will(returnValue(IgnoreState.NORMATIVE));
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(!expected));

            one(broadcaster).handleEvent(new FailedAssertBooleanEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected, !expected));
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    private void assertSimpleHappyPath(final boolean expected) {
        AssertBooleanCommand command = new AssertBooleanCommand(expected);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        context.checking(new Expectations(){{
            allowing(evalContext).getIgnoreState();
            will(returnValue(IgnoreState.NORMATIVE));
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(expected));

            one(broadcaster).handleEvent(new SuccessfulAssertBooleanEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected));
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testEvalResultIsNotABoolean() {
        AssertBooleanCommand command = new AssertBooleanCommand(true);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        context.checking(new Expectations(){{
            allowing(evalContext).getIgnoreState();
            will(returnValue(IgnoreState.NORMATIVE));
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(42));

            one(broadcaster).handleEvent(new FailedAssertBooleanEvent(emptyElement, IgnoreState.NORMATIVE, expression, true, 42));
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testEvalResultIsNull() {
        AssertBooleanCommand command = new AssertBooleanCommand(true);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        context.checking(new Expectations(){{
            allowing(evalContext).getIgnoreState();
            will(returnValue(IgnoreState.NORMATIVE));
            one(evalContext).eval(expression, emptyElement);
            will(returnValue(null));

            one(broadcaster).handleEvent(new FailedAssertBooleanEvent(emptyElement, IgnoreState.NORMATIVE, expression, true, null));
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

    public void testEvalBlowsUp() {
        AssertBooleanCommand command = new AssertBooleanCommand(true);
        final EvaluationContext evalContext = context.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final Throwable error = new Error("this is only a test");

        context.checking(new Expectations(){{
            allowing(evalContext).getIgnoreState();
            will(returnValue(IgnoreState.NORMATIVE));
            one(evalContext).eval(expression, emptyElement);
            will(throwException(error));

            one(broadcaster).handleEvent(new ExceptionThrownEvent(emptyElement, IgnoreState.NORMATIVE, expression, error));
        }});

        command.run(xcordion, emptyElement, evalContext, expression);
    }

}
