package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;


public class AssertEqualsCommand extends ChildrenInSetupRunVerifyOrderCommand {

	@Override
	public CommandType getCommandType() {
		return CommandType.VERIFY;
	}

	@Override
	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void run(Xcordion<T> xcordion, T target, C context, String expression) {
		Object actual = context.eval(expression, target);
        //Object expected = context.getValue(target, actual != null ? actual.getClass() : null);
        Object expected = context.getValue(target, null);

        // TODO: is coercing to string a cop-out?
        String actualString = (actual != null) ? actual.toString() : "";
        String expectedString = (expected != null) ? expected.toString() : "";

        if (expectedString.equals(actualString)) {
			xcordion.getBroadcaster().successfulAssertEquals(target, expression, expected);
		} else {
			xcordion.getBroadcaster().failedAssertEquals(target, expression, expected, actual);
		}
	}

}