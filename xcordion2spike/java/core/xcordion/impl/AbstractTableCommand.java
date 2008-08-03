package xcordion.impl;

import xcordion.api.*;

public abstract class AbstractTableCommand extends AbstractCommand {

	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void runRow(Xcordion<T> xcordion, TableNavigator<T> table, RowNavigator<T> row, C context, String expression) {
		runRow(xcordion, table, row, context, expression, CommandType.ALL);
	}

	protected <T extends TestElement<T>, C extends EvaluationContext<C>> void runRow(Xcordion<T> xcordion, TableNavigator<T> table, RowNavigator<T> row, C context, String expression, CommandType commandType) {
		for (int column = 0; column < table.getOriginalWidth(); column++) {
			T cell = row.get(column);
			CommandAndExpression command = xcordion.getCommandRepository().commandForElement(cell);
			if (command == null) {
				for (T header : table.getColumnHeaders(column)) {
					command = xcordion.getCommandRepository().commandForElement(header);
					if (command != null) {
						break;
					}
				}
			}

			if (command != null) {
				if (commandType == CommandType.ALL || command.getCommand().getCommandType() == commandType) {
					command.command.runElementAndChildren(xcordion, cell, context, command.expression);
				}
			} else {
				runChildren(xcordion, cell.getChildren(), context, commandType);
			}
		}
	}

}
