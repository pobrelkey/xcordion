package xcordion.impl.command;

import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.impl.AbstractCommand;

abstract public class ChildrenInDocumentOrderCommand extends AbstractCommand {

	@Override
	public <T extends TestElement<T>, C extends EvaluationContext<C>> void runElementAndChildren(Xcordion<T> xcordion, T target, C context, String expression) {
		try {
			run(xcordion, target, context, expression);
		} catch (Throwable e) {
			xcordion.getBroadcaster().handleEvent(new ExceptionThrownEvent<T>(target, context.getIgnoreState(), expression, e));
		}
		runChildren(xcordion, target.getChildren(), context);
	}

	protected abstract <T extends TestElement<T>, C extends EvaluationContext<C>> void run(Xcordion<T> xcordion, T target, C context, String expression);

}
