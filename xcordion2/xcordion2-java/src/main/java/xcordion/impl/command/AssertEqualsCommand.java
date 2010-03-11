package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.FailedAssertEqualsEvent;
import xcordion.api.events.SuccessfulAssertEqualsEvent;


public class AssertEqualsCommand extends ChildrenInSetupRunVerifyOrderCommand {

	@Override
	public CommandType getCommandType() {
		return CommandType.VERIFY;
	}

	@Override
	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void run(Xcordion<T> xcordion, T target, C context, String expression) {
        Object actual;
        try {
            actual = context.eval(expression, target);
        } catch (Throwable e) {
            xcordion.getBroadcaster().handleEvent(new ExceptionThrownEvent<T>(target, context.getIgnoreState(), expression, e));
            return;
        }

        //Object expected = context.getValue(target, actual != null ? actual.getClass() : null);
        Object expected = context.getValue(target, null);

        // TODO: is coercing to string a cop-out?
        String actualString = (actual != null) ? actual.toString() : "";
        String expectedString = (expected != null) ? expected.toString() : "";

        if (expectedString.equals(actualString)) {
			xcordion.getBroadcaster().handleEvent(new SuccessfulAssertEqualsEvent<T>(target, context.getIgnoreState(), expression, expected));
		} else {
			xcordion.getBroadcaster().handleEvent(new FailedAssertEqualsEvent<T>(target, context.getIgnoreState(), expression, expected, actual));
		}
	}

}
