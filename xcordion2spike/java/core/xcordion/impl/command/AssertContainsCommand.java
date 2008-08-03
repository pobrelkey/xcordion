package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;


public class AssertContainsCommand extends ChildrenInSetupRunVerifyOrderCommand {

	private boolean negate;

	public AssertContainsCommand(boolean negate) {
		this.negate = negate;
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.VERIFY;
	}

	@Override
	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void run(Xcordion<T> xcordion, T target, C context, String expression) {
        Object actual, expected;
        try {
            actual = context.eval(expression, target);
            expected = context.getValue(target, String.class);
        } catch (Throwable e) {
            xcordion.getBroadcaster().exception(target, expression, e);
            return;
        }


        boolean result;
        if (expected == null || expected.toString().equals("")) {
            // vacuously true - any string contains the empty string
            result = true;
        } else {
            result = (actual != null) && (actual.toString().indexOf(expected.toString()) != -1);
        }

        result ^= negate;

        if (result) {
			xcordion.getBroadcaster().successfulAssertContains(target, expression, expected, actual);
		} else {
			xcordion.getBroadcaster().failedAssertContains(target, expression, expected, actual);
		}
	}

}