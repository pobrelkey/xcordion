package xcordion.impl.command;

import xcordion.api.EvaluationContext;
import xcordion.api.IgnoreState;
import xcordion.api.events.SuccessfulAssertContainsEvent;
import xcordion.api.events.FailedAssertContainsEvent;
import xcordion.api.events.ExceptionThrownEvent;
import org.mockito.Mockito;
import org.junit.Test;

public class AssertContainsCommandTest extends AbstractCommandTest {

    @Test
    public void testSimpleHappyPath() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "tally";
        final String actual = "totally mexico";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(actual);
        Mockito.when(evalContext.getValue(emptyElement, String.class)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(evalContext, Mockito.atMost(1)).getValue(emptyElement, String.class);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new SuccessfulAssertContainsEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected, actual));
    }

    @Test
    public void testSimpleSadPath() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "tally";
        final String actual = "awesome welles";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(actual);
        Mockito.when(evalContext.getValue(emptyElement, String.class)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(evalContext, Mockito.atMost(1)).getValue(emptyElement, String.class);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new FailedAssertContainsEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected, actual));
    }

    @Test
    public void testEvalReturnsNull() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "tally";
        final String actual = null;

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(actual);
        Mockito.when(evalContext.getValue(emptyElement, String.class)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(evalContext, Mockito.atMost(1)).getValue(emptyElement, String.class);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new FailedAssertContainsEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected, actual));
    }

    @Test
    public void testEvalBlowsUp() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final Throwable error = new Error("this is only a test");

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenThrow(error);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new ExceptionThrownEvent(emptyElement, IgnoreState.NORMATIVE, expression, error));
    }

    @Test
    public void testExpectedIsNull() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = null;
        final String actual = "today ridicule, tomorrow really cool";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(actual);
        Mockito.when(evalContext.getValue(emptyElement, String.class)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(evalContext, Mockito.atMost(1)).getValue(emptyElement, String.class);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new SuccessfulAssertContainsEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected, actual));
    }

    @Test
    public void testNonStringOperands() {
        AssertContainsCommand command = new AssertContainsCommand(false);
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final Object expected = 1337;
        final Object actual = 31337357;

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(actual);
        Mockito.when(evalContext.getValue(emptyElement, String.class)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.atMost(1)).eval(expression, emptyElement);
        Mockito.verify(evalContext, Mockito.atMost(1)).getValue(emptyElement, String.class);
        Mockito.verify(broadcaster, Mockito.atMost(1)).handleEvent(new SuccessfulAssertContainsEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected, actual));
    }
}
