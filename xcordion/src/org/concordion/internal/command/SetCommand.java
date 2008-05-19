package org.concordion.internal.command;


import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Element;
import org.concordion.internal.CommandCall;
import org.concordion.internal.CommandCallList;

public class SetCommand extends AbstractCommand {
    private static final ValueGetter DEFAULT_GETTER = new ValueGetter() {
        public Object valueOf(Element e) {
            return e.getText();
        }
    };

    private ValueGetter getter;

    // TODO Rob: PLEASE suggest a better name for this interface - I'm too fried to think of one right now.
    public interface ValueGetter {
        Object valueOf(Element e);
    }

    public SetCommand() {
        this(DEFAULT_GETTER);
    }

    public SetCommand(ValueGetter getter) {
        this.getter = getter;
    }

    public void setGetter(ValueGetter getter) {
        this.getter = getter;
    }

    private ValueGetter getGetter() {
        return (getter != null) ? getter : DEFAULT_GETTER;
    }

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        CommandCallList childCommands = commandCall.getChildren();
        childCommands.setUp(evaluator, resultRecorder);
        evaluator.setVariable(commandCall.getExpression(), getGetter().valueOf(commandCall.getElement()));
        childCommands.execute(evaluator, resultRecorder);
        childCommands.verify(evaluator, resultRecorder);
    }
}
