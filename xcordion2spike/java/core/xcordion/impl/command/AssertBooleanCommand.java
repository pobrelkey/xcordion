package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.SuccessfulAssertBooleanEvent;
import xcordion.api.events.FailedAssertBooleanEvent;
import xcordion.util.Coercions;


public class AssertBooleanCommand extends ChildrenInSetupRunVerifyOrderCommand {

	private boolean value;

	public AssertBooleanCommand(boolean value) {
		this.value = value;
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.VERIFY;
	}

	@Override
	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void run(Xcordion<T> xcordion, T target, C context, String expression) {
        Object result;
        try {
            result = context.eval(expression, target);
        } catch (Throwable e) {
            xcordion.getBroadcaster().handleEvent(new ExceptionThrownEvent<T>(target, context.getIgnoreState(), expression, e));
            return;
        }

        Boolean booleanResult = Coercions.toBoolean(result);
        if (booleanResult != null && booleanResult == value) {
			xcordion.getBroadcaster().handleEvent(new SuccessfulAssertBooleanEvent<T>(target, context.getIgnoreState(), expression, value));
		} else {
			xcordion.getBroadcaster().handleEvent(new FailedAssertBooleanEvent<T>(target, context.getIgnoreState(), expression, value, result));
		}
	}

}
