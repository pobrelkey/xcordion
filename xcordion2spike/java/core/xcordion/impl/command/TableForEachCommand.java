package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.impl.*;

import java.util.Iterator;

public class TableForEachCommand extends AbstractTableCommand {

	@Override
	public CommandType getCommandType() {
		return CommandType.VERIFY;
	}

	@Override
	public <T extends TestElement<T>, C extends EvaluationContext<C>> void runElementAndChildren(Xcordion<T> xcordion, T target, C context, String expression) {
		Iterator<C> iterator;
		try {
			iterator = context.iterate(expression, target).iterator();
		} catch (Exception e) {
			xcordion.getBroadcaster().exception(target, expression, e);
			return;
		}

		TableNavigator<T> table = new TableNavigator<T>(target);
		while (iterator.hasNext()) {
			boolean isSurplusRow = !table.hasMoreContentRows();
			RowNavigatorImpl<T> row = table.nextContentRow();
			if (isSurplusRow) {
				xcordion.getBroadcaster().surplusRow(row);
			}
			try {
				C rowContext = iterator.next();
				runRow(xcordion, table, row, rowContext, expression);
			} catch (Exception e) {
				xcordion.getBroadcaster().exception(row.getSidecarCell(), expression, e);
			}
		}
		while (table.hasMoreContentRows()) {
			xcordion.getBroadcaster().missingRow(table.nextContentRow());
		}
	}

}
