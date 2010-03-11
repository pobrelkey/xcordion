package xcordion.impl.command;

import org.junit.Test;
import org.mockito.Mockito;
import xcordion.api.EvaluationContext;
import xcordion.api.IgnoreState;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.SuccessfulExecuteEvent;

public class ExecCommandTest extends AbstractCommandTest {
    @Test
    public void testSimpleHappyPath() {
        ExecCommand command = new ExecCommand();
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenReturn(null);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.times(1)).eval(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.times(1)).handleEvent(new SuccessfulExecuteEvent(emptyElement, IgnoreState.NORMATIVE, expression));
        Mockito.verifyNoMoreInteractions(broadcaster);
    }

    @Test
    public void testSimpleSadPath() {
        ExecCommand command = new ExecCommand();
        final EvaluationContext evalContext = Mockito.mock(EvaluationContext.class);
        final String expression = "someExpression()";
        RuntimeException error = new RuntimeException("this is only a test");

        Mockito.when(evalContext.getIgnoreState()).thenReturn(IgnoreState.NORMATIVE);
        Mockito.when(evalContext.eval(expression, emptyElement)).thenThrow(error);

        command.run(xcordion, emptyElement, evalContext, expression);

        Mockito.verify(evalContext, Mockito.times(1)).eval(expression, emptyElement);
        Mockito.verify(broadcaster, Mockito.times(1)).handleEvent(new ExceptionThrownEvent(emptyElement, IgnoreState.NORMATIVE, expression, error));
        Mockito.verifyNoMoreInteractions(broadcaster);
    }
}
