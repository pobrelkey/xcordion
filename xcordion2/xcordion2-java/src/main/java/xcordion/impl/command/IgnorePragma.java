package xcordion.impl.command;

import xcordion.api.EvaluationContext;
import xcordion.api.IgnoreState;
import xcordion.api.Pragma;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.api.events.ChangedIgnoreStateEvent;
import xcordion.api.events.ExceptionThrownEvent;

public class IgnorePragma implements Pragma {
    public <T extends TestElement<T>, C extends EvaluationContext<C>> C evaluate(Xcordion<T> xcordion, T target, C context, String expression) {
        expression = expression.toLowerCase().trim();
        IgnoreState ignoreState;
        if (expression.equalsIgnoreCase("no")) {
            ignoreState = IgnoreState.NORMATIVE;
        } else if (expression.equalsIgnoreCase("yes") || expression.equalsIgnoreCase("ignore")) {
            ignoreState = IgnoreState.IGNORED;
        } else if (expression.equalsIgnoreCase("omit")) {
            ignoreState = IgnoreState.OMITTED;
        } else {
            // TODO: better event type
            xcordion.getBroadcaster().handleEvent(new ExceptionThrownEvent<T>(target, context.getIgnoreState(), expression, new Exception("Ignore attribute should be one of 'no', 'yes', 'ignore' or 'omit'")));
            return context;
        }

        xcordion.getBroadcaster().handleEvent(new ChangedIgnoreStateEvent<T>(target, ignoreState));
        return context.withIgnoreState(ignoreState);
    }
}
