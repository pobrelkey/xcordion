package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.SuccessfulExecuteEvent;


public class ExecCommand extends ChildrenInSetupRunVerifyOrderCommand {

	@Override
	public CommandType getCommandType() {
		return CommandType.EXECUTE;
	}

	@Override
	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void run(Xcordion<T> xcordion, T target, C context, String expression) {
        try {
            context.eval(expression, target);
            xcordion.getBroadcaster().handleEvent(new SuccessfulExecuteEvent<T>(target, context.getIgnoreState(), expression));
        } catch (Throwable e) {
            xcordion.getBroadcaster().handleEvent(new ExceptionThrownEvent<T>(target, context.getIgnoreState(), expression, e));
            return;
        }
	}

}
