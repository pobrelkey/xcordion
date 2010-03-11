package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.api.events.InsertTextEvent;


public class InsertTextCommand extends ChildrenInSetupRunVerifyOrderCommand {

	@Override
	public CommandType getCommandType() {
		return CommandType.VERIFY;
	}

	@Override
	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void run(Xcordion<T> xcordion, T target, C context, String expression) {
		Object result = context.eval(expression, target);
		xcordion.getBroadcaster().handleEvent(new InsertTextEvent<T>(target, context.getIgnoreState(), expression, result));
	}

}
