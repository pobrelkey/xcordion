package xcordion.impl;

import xcordion.impl.CommandFactory;
import xcordion.api.TestElement;
import xcordion.api.Command;

public class DefaultCommandFactory implements CommandFactory {

    private Command command;

    public DefaultCommandFactory(Command command) {
        this.command = command;
    }

    public <T extends TestElement<T>> Command commandForElement(T element) {
        return command;
    }

}
