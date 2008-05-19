package org.concordion.internal.command;

import org.concordion.internal.CommandCall;
import org.concordion.internal.util.Check;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Element;

public class InsertTextCommand extends AbstractCommand {

    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'insertText' is not supported");

        Object result = evaluator.evaluate(commandCall.getExpression());

        Element element = commandCall.getElement();
        element.removeChildren();
        element.addStyleClass("inserted");
        if (result != null) {
            element.appendText(result.toString());
        } else {
            Element child = new Element("i");
            child.appendText("(null)");
            element.appendChild(child);
        }
    }

}
