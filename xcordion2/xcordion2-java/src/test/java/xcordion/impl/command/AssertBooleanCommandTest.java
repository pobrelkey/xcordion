package xcordion.impl.command;

import org.junit.Test;
import org.mockito.Mockito;
import xcordion.api.EvaluationContext;
import xcordion.api.IgnoreState;
import xcordion.api.events.FailedAssertBooleanEvent;
import xcordion.api.events.SuccessfulAssertBooleanEvent;
import xcordion.api.events.ExceptionThrownEvent;

public class AssertBooleanCommandTest extends AbstractCommandTest {

    @Test
    public void testExpectTrueIsFalse() {
        assertSimpleSadPath(true);
    }

    @Test
    public void testExpectFalseIsTrue() {
        assertSimpleSadPath(false);
    }

    @Test
    public void testExpectTrueIsTrue() {
        assertSimpleHappyPath(true);
    }

    @Test
    public void testExpectFalseIsFalse() {
        assertSimpleHappyPath(false);
    }

    private void assertSimpleSadPath(final boolean expected) {
        AssertBooleanCommand command = new AssertBooleanCommand(expected);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(!expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new FailedAssertBooleanEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected, !expected));
    }

    private void assertSimpleHappyPath(final boolean expected) {
        AssertBooleanCommand command = new AssertBooleanCommand(expected);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new SuccessfulAssertBooleanEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected));
    }

    @Test
    public void testEvalResultIsNotABoolean() {
        AssertBooleanCommand command = new AssertBooleanCommand(true);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(42);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new FailedAssertBooleanEvent(emptyElement, IgnoreState.NORMATIVE, expression, true, 42));
    }

    @Test
    public void testEvalResultIsNull() {
        AssertBooleanCommand command = new AssertBooleanCommand(true);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(null);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new FailedAssertBooleanEvent(emptyElement, IgnoreState.NORMATIVE, expression, true, null));
    }

    @Test
    public void testEvalBlowsUp() {
        AssertBooleanCommand command = new AssertBooleanCommand(true);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final Throwable error = new Error("this is only a test");

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenThrow(error);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new ExceptionThrownEvent(emptyElement, IgnoreState.NORMATIVE, expression, error));
    }

}
