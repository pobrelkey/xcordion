package xcordion.impl.command;

import xcordion.api.*;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.impl.*;

public class TableExecuteCommand extends AbstractTableCommand {

	@Override
	public CommandType getCommandType() {
		return CommandType.EXECUTE;
	}

	@Override
	public <T extends TestElement<T>, C extends EvaluationContext<C>> void runElementAndChildren(Xcordion<T> xcordion, T target, C context, String expression) {
		TableNavigator<T> table = new TableNavigator<T>(target, xcordion.getCommandRepository());
		while (table.hasMoreContentRows()) {
			RowNavigator<T> row = table.nextContentRow();
			try {
				runRow(xcordion, table, row, context, CommandType.SETUP);
				context.eval(expression, target);
				runRow(xcordion, table, row, context, CommandType.EXECUTE);
				runRow(xcordion, table, row, context, CommandType.VERIFY);
			} catch (Exception e) {
                xcordion.getBroadcaster().handleEvent(new ExceptionThrownEvent<T>(row.getSidecarCell(), context.getIgnoreState(), expression, e));
			}
		}
	}

}
