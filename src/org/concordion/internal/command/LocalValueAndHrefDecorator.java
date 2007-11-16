package org.concordion.internal.command;

import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

public class LocalValueAndHrefDecorator extends AbstractCommandDecorator {

    private static final String VALUE_VARIABLE = "#VALUE";
    private static final String HREF_VARIABLE  = "#HREF";

    @Override
    protected void process(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Runnable runnable) {
        Object savedValue = evaluator.getVariable(VALUE_VARIABLE);
        Object savedHref = evaluator.getVariable(HREF_VARIABLE);
        String href = null;
        try {
            evaluator.setVariable(VALUE_VARIABLE, commandCall.getElement().getText());
            href = commandCall.getElement().getAttributeValue("href");
            if (href != null) {
                evaluator.setVariable(HREF_VARIABLE, href);
            }
            runnable.run();
        } finally {
            evaluator.setVariable(VALUE_VARIABLE, savedValue);
            if (href != null) {
                evaluator.setVariable(HREF_VARIABLE, savedHref);
            }
        }
    }
}
