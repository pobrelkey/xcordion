package org.concordion.internal;

import org.concordion.api.Command;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Resource;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.util.Check;

import java.util.List;

/**
 * Nested CommandCalls form an abstract syntax tree. (The XML is the concrete
 * syntax tree.)
 */
public class CommandCall {

    private final CommandCallList children = new CommandCallList();
    private final Command command;
    private final String expression;
    private final Resource resource;
    private Element element;

    public CommandCall(Command command, Element element, String expression, Resource resource) {
        this.command = command;
        this.element = element;
        this.expression = expression;
        this.resource = resource;
    }

    public void setUp(Evaluator evaluator, ResultRecorder resultRecorder) {
        command.setUp(this, evaluator, resultRecorder);
    }

    public void execute(Evaluator evaluator, ResultRecorder resultRecorder) {
        command.execute(this, evaluator, resultRecorder);
    }

    public void verify(Evaluator evaluator, ResultRecorder resultRecorder) {
        command.verify(this, evaluator, resultRecorder);
    }

    public void appendChild(CommandCall commandNode) {
        children.add(commandNode);
    }

    public CommandCallList getChildren() {
        return children;
    }

    public Command getCommand() {
        return command;
    }

    public Element getElement() {
        return element;
    }

    public String getExpression() {
        return expression;
    }

    public Resource getResource() {
        return resource;
    }

    public boolean hasChildCommands() {
        return !children.isEmpty();
    }

    public void setElement(Element element) {
        Check.notNull(element, "element is null");
        this.element = element;
    }

    public CommandCall clone() {
        if (!children.isEmpty()) {
            throw new IllegalStateException("cannot duplicate a CommandCall with children");
        }
        return new CommandCall(command, element, expression, resource);
    }

    public void setChildren(List<CommandCall> commandCalls) {
        children.clear();
        children.addAll(commandCalls);
    }

    public void removeChildren() {
        children.clear();
    }
}
