package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;

public class NoOpCommand extends ChildrenInDocumentOrderCommand {

	@Override
	public CommandType getCommandType() {
		return CommandType.EXECUTE;
	}

	@Override
	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void run(Xcordion<T> xcordion, T target, C context, String expression) {
		// do nothing - we're the no-op command!
	}

}
