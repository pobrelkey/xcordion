package xcordion.impl;

import xcordion.api.Xcordion;
import xcordion.api.*;


abstract public class AbstractCommand implements Command {

	abstract public CommandType getCommandType();
	abstract public <T extends TestElement<T>, C extends EvaluationContext<C>> void runElementAndChildren(Xcordion<T> xcordion, T target, C context, String expression);

	
	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void runChildren(Xcordion<T> xcordion, Iterable<T> elements, C context) {
		runChildren(xcordion, elements, context, CommandType.ALL);
	}

	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void runChildren(Xcordion<T> xcordion, Iterable<T> elements, C context, CommandType commandType) {
		for (T element : elements) {
			runElement(xcordion, element, commandType, context);
		}
	}

	private <T extends TestElement<T>, C extends EvaluationContext<C>> void runElement(Xcordion<T> xcordion, T element, CommandType commandType, C context) {
        for (ItemAndExpression<Pragma> pragmaAndExpression : xcordion.getCommandRepository().pragmasForElement(element)) {
            context = pragmaAndExpression.getItem().evaluate(xcordion, element, context, pragmaAndExpression.getExpression());
        }
		ItemAndExpression<Command> commandAndExpression = xcordion.getCommandRepository().commandForElement(element, context.getIgnoreState());
		if (commandAndExpression != null) {
			if (commandType == CommandType.ALL || commandAndExpression.getItem().getCommandType() == commandType) {
				commandAndExpression.getItem().runElementAndChildren(xcordion, element, context, commandAndExpression.getExpression());
			}
		} else {
			runChildren(xcordion, element.getChildren(), context, commandType);
		}
	}
	
}
