package org.concordion.internal.command;

import org.concordion.internal.CommandCall;
import org.concordion.internal.CommandCallList;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.Announcer;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Element;
import org.concordion.api.Result;

public class AssertBooleanCommand extends AbstractCommand {
    private Announcer<AssertBooleanListener> listeners = Announcer.to(AssertBooleanListener.class);
    private boolean expected;

    public AssertBooleanCommand(boolean desiredResult) {
        this.expected = desiredResult;
    }

    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        //Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'assert" + (expected ? "True" : "False") + "' is not supported");
        CommandCallList childCommands = commandCall.getChildren();

        Element element = commandCall.getElement();
        childCommands.setUp(evaluator, resultRecorder);
        childCommands.execute(evaluator, resultRecorder);
        evaluate(commandCall, evaluator, resultRecorder, element);
        childCommands.verify(evaluator, resultRecorder);
    }

    private void evaluate(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Element element) {
        Object actualObject = evaluator.evaluate(commandCall.getExpression());
        boolean actual;
        if (actualObject instanceof Boolean) {
            actual = ((Boolean) actualObject).booleanValue();
        } else if (actualObject != null) {
            actual = Boolean.toString(expected).equals(actualObject.toString());
        } else {
            actual = !expected;
        }

        if (actual == expected) {
            resultRecorder.record(Result.SUCCESS);
            announceSuccess(element);
        } else {
            resultRecorder.record(Result.FAILURE);
            announceFailure(element, commandCall.getExpression(), actual);
        }
    }

    public void addAssertBooleanListener(AssertBooleanListener listener) {
        listeners.addListener(listener);
    }

    public void removeAssertBooleanListener(AssertBooleanListener listener) {
        listeners.removeListener(listener);
    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertBooleanSuccessEvent(element));
    }

    private void announceFailure(Element element, String expression, Object actual) {
        listeners.announce().failureReported(new AssertBooleanFailureEvent(element, expression, actual));
    }


}
