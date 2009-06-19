package xcordion.impl;

import xcordion.api.*;

import java.util.List;

public abstract class AbstractTableCommand extends AbstractCommand {

	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void runRow(Xcordion<T> xcordion, TableNavigator<T> table, RowNavigator<T> row, C context) {
		runRow(xcordion, table, row, context, CommandType.ALL);
	}

	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void runRow(Xcordion<T> xcordion, TableNavigator<T> table, RowNavigator<T> row, C context, CommandType commandType) {
		for (ItemAndExpression<Pragma> pragma : row.getPragmas()) {
            context = pragma.getItem().evaluate(xcordion, row.getRowElement(), context, pragma.getExpression());
        }

        for (int column = 0; column < table.getOriginalWidth(); column++) {
			T cell = row.get(column);

            List<ItemAndExpression<Pragma>> pragmasList = xcordion.getCommandRepository().pragmasForElement(cell);
            for (T header : table.getColumnHeaders(column)) {
                pragmasList.addAll(xcordion.getCommandRepository().pragmasForElement(header));
            }
            for (ItemAndExpression<Pragma> pragmaAndExpression : pragmasList) {
                context = pragmaAndExpression.getItem().evaluate(xcordion, cell, context, pragmaAndExpression.getExpression());
            }

			ItemAndExpression<Command> commandAndExpression = xcordion.getCommandRepository().commandForElement(cell, context.getIgnoreState());
			if (commandAndExpression == null) {
				for (T header : table.getColumnHeaders(column)) {
					commandAndExpression = xcordion.getCommandRepository().commandForElement(header, context.getIgnoreState());
					if (commandAndExpression != null) {
						break;
					}
				}
			}

			if (commandAndExpression != null) {
				if (commandType == CommandType.ALL || commandAndExpression.getItem().getCommandType() == commandType) {
					commandAndExpression.getItem().runElementAndChildren(xcordion, cell, context, commandAndExpression.getExpression());
				}
			} else {
				runChildren(xcordion, cell.getChildren(), context, commandType);
			}
		}
	}

}
