package xcordion.impl.command;

import xcordion.api.CommandType;
import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.Xcordion;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.SurplusRowEvent;
import xcordion.api.events.MissingRowEvent;
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
            xcordion.getBroadcaster().handleEvent(new ExceptionThrownEvent<T>(target, context.getIgnoreState(), expression, e));
			return;
		}

		TableNavigator<T> table = new TableNavigator<T>(target, xcordion.getCommandRepository());
		while (iterator.hasNext()) {
			boolean isSurplusRow = !table.hasMoreContentRows();
			RowNavigatorImpl<T> row = table.nextContentRow();
			if (isSurplusRow) {
				xcordion.getBroadcaster().handleEvent(new SurplusRowEvent<T>(row.getRowElement(), context.getIgnoreState()));
			}
			try {
				C rowContext = iterator.next();
				runRow(xcordion, table, row, rowContext);
			} catch (Exception e) {
                xcordion.getBroadcaster().handleEvent(new ExceptionThrownEvent<T>(row.getSidecarCell(), context.getIgnoreState(), expression, e));
			}
		}
		while (table.hasMoreContentRows()) {
			xcordion.getBroadcaster().handleEvent(new MissingRowEvent<T>(table.nextContentRow().getRowElement(), context.getIgnoreState()));
		}
	}

}
