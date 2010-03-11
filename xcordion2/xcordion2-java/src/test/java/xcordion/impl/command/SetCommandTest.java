package xcordion.impl.command;

import org.junit.Test;
import org.mockito.Mockito;
import xcordion.api.EvaluationContext;
import xcordion.api.IgnoreState;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.SuccessfulSetEvent;

public class SetCommandTest extends AbstractCommandTest {
    @Test
    public void testSimpleHappyPath() {
        SetCommand command = new SetCommand();
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        final String text = "some text";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.set(expression, emptyElement)).thenReturn(text);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.times(1)).set(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.times(1)).handleEvent(new SuccessfulSetEvent(emptyElement, IgnoreState.NORMATIVE, expression, text));
        Mockito.verifyNoMoreInteractions(broadcaster);
    }

    @Test
    public void testSimpleSadPath() {
        SetCommand command = new SetCommand();
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        RuntimeException error = new RuntimeException("this is only a test");

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.set(expression, emptyElement)).thenThrow(error);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.times(1)).set(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.times(1)).handleEvent(new ExceptionThrownEvent(emptyElement, IgnoreState.NORMATIVE, expression, error));
        Mockito.verifyNoMoreInteractions(broadcaster);
    }
}
