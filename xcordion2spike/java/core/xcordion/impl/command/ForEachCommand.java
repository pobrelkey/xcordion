package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.impl.AbstractCommand;

public class ForEachCommand extends AbstractCommand {

	@Override
	public CommandType getCommandType() {
		return CommandType.EXECUTE;
	}

	@Override
	public <T extends TestElement<T>, C extends EvaluationContext<C>> void runElementAndChildren(Xcordion<T> xcordion, T target, C context, String expression) {
		Iterable<C> iterable;
		try {
			iterable = context.iterate(expression, target);
		} catch (Exception e) {
			xcordion.getBroadcaster().exception(target, expression, e);
			return;
		}

		T parent = target.getParent();
		T placeholder = target.getDocument().newElement("span");
        parent.insertChildAfter(target, placeholder);
		T lastSibling = placeholder;
		T prototype = target.duplicate();
		parent.remove(target);
		for (C itemContext : iterable) {
            T newContent = prototype.duplicate();
            parent.insertChildAfter(lastSibling, newContent);
            runChildren(xcordion, newContent.getChildren(), itemContext);
            lastSibling = newContent;
		}
		parent.remove(placeholder);
	}

}
