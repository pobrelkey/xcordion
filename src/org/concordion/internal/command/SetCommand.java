package org.concordion.internal.command;


import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.CommandCall;
import org.concordion.internal.CommandCallList;

public class SetCommand extends AbstractCommand {

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        CommandCallList childCommands = commandCall.getChildren();
        childCommands.setUp(evaluator, resultRecorder);
        evaluator.setVariable(commandCall.getExpression(), commandCall.getElement().getText());
        childCommands.execute(evaluator, resultRecorder);
        childCommands.verify(evaluator, resultRecorder);
    }
}
