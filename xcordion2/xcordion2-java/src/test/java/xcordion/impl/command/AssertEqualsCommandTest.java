package xcordion.impl.command;

import org.junit.Test;
import org.mockito.Mockito;
import xcordion.api.EvaluationContext;
import xcordion.api.IgnoreState;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.FailedAssertEqualsEvent;
import xcordion.api.events.SuccessfulAssertEqualsEvent;

public class AssertEqualsCommandTest extends AbstractCommandTest {
    @Test
    public void testSimpleHappyPath() {
        AssertEqualsCommand command = new AssertEqualsCommand();
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "blue";
        final String actual = "blue";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(actual);
        Mockito.when(evalContext.getValue(emptyElement, null)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.times(1)).eval(expression, emptyElement);
        Mockito.verify(evalContext, Mockito.times(1)).getValue(emptyElement, null);
        Mockito.verify(broadcaster, Mockito.times(1)).handleEvent(new SuccessfulAssertEqualsEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected));
        Mockito.verifyNoMoreInteractions(broadcaster);
    }

    @Test
    public void testSimpleSadPath() {
        AssertEqualsCommand command = new AssertEqualsCommand();
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "blue";
        final String actual = "red";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(actual);
        Mockito.when(evalContext.getValue(emptyElement, null)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.times(1)).eval(expression, emptyElement);
        Mockito.verify(evalContext, Mockito.times(1)).getValue(emptyElement, null);
        Mockito.verify(broadcaster, Mockito.times(1)).handleEvent(new FailedAssertEqualsEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected, actual));
        Mockito.verifyNoMoreInteractions(broadcaster);
    }
    
    @Test
    public void testEvalReturnsNull() {
        AssertEqualsCommand command = new AssertEqualsCommand();
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = "blue";
        final String actual = null;

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(actual);
        Mockito.when(evalContext.getValue(emptyElement, null)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.times(1)).eval(expression, emptyElement);
        Mockito.verify(evalContext, Mockito.times(1)).getValue(emptyElement, null);
        Mockito.verify(broadcaster, Mockito.times(1)).handleEvent(new FailedAssertEqualsEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected, actual));
        Mockito.verifyNoMoreInteractions(broadcaster);
    }

    @Test
    public void testEvalBlowsUp() {
        AssertEqualsCommand command = new AssertEqualsCommand();
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final Throwable error = new Error("this is only a test");

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenThrow(error);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.times(1)).eval(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.times(1)).handleEvent(new ExceptionThrownEvent(emptyElement, IgnoreState.NORMATIVE, expression, error));
        Mockito.verifyNoMoreInteractions(broadcaster);
    }

    @Test
    public void testExpectedIsNull() {
        AssertEqualsCommand command = new AssertEqualsCommand();
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String expected = null;
        final String actual = null;

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(actual);
        Mockito.when(evalContext.getValue(emptyElement, null)).thenReturn(expected);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.times(1)).eval(expression, emptyElement);
        Mockito.verify(evalContext, Mockito.times(1)).getValue(emptyElement, null);
        Mockito.verify(broadcaster, Mockito.times(1)).handleEvent(new SuccessfulAssertEqualsEvent(emptyElement, IgnoreState.NORMATIVE, expression, expected));
        Mockito.verifyNoMoreInteractions(broadcaster);
    }
    
}
