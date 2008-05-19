package org.concordion.internal;

import org.concordion.api.Command;
import org.concordion.api.CommandDecorator;
import org.concordion.api.CommandFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry implements CommandFactory {

    private Map<Object, Command> commandMap = new HashMap<Object, Command>();
    private ArrayList<CommandDecorator> decorators = new ArrayList<CommandDecorator>();

    public CommandRegistry register(String namespaceURI, String commandName, Command command) {
        commandMap.put(makeKey(namespaceURI, commandName), command);
        return this;
    }

    public Command createCommand(String namespaceURI, String commandName) {
        Command command = commandMap.get(makeKey(namespaceURI, commandName));
        if (command == null) {
            return null;
        }
        // iterate in reverse so that internal decroators, e.g. ThrowableCatchingDecorator, are outermost
        for (int i = decorators.size() - 1; i >= 0; i--) {
            command = decorators.get(i).decorate(command);
        }
        return command;
    }

    private Object makeKey(String namespaceURI, String commandName) {
        return namespaceURI + " " + commandName;
    }

    public void addDecorator(CommandDecorator decorator) {
        decorators.add(decorator);
    }
}
