package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.SuccessfulSetEvent;


public class SetCommand extends ChildrenInSetupRunVerifyOrderCommand {

	@Override
	public CommandType getCommandType() {
		return CommandType.SETUP;
	}

	@Override
	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void run(Xcordion<T> xcordion, T target, C context, String expression) {
        try {
            Object value = context.set(expression, target);
            xcordion.getBroadcaster().handleEvent(new SuccessfulSetEvent<T>(target, context.getIgnoreState(), expression, value));
        } catch (Throwable e) {
            xcordion.getBroadcaster().handleEvent(new ExceptionThrownEvent<T>(target, context.getIgnoreState(), expression, e));
            return;
        }
	}

}
