package xcordion.impl;

import xcordion.impl.CommandFactory;
import xcordion.api.TestElement;
import xcordion.api.Command;

public class TableCommandFactory implements CommandFactory {
    private Command tableCommand;
    private Command command;

    public TableCommandFactory(Command tableCommand, Command command) {
        this.tableCommand = tableCommand;
        this.command = command;
    }

    public <T extends TestElement<T>> Command commandForElement(T element) {
        if (element.getLocalName().toLowerCase().equals("table")) {
            return tableCommand;
        } else {
            return command;
        }
    }
}
