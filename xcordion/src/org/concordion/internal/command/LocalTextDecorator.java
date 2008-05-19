package org.concordion.internal.command;

import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.CommandCall;

public class LocalTextDecorator extends AbstractCommandDecorator {

    @Override
    protected void process(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Runnable runnable) {
        Object savedValue = evaluator.getVariable("TEXT");
        try {
            evaluator.setVariable("#TEXT", commandCall.getElement().getText());
            runnable.run();
        } finally {
            evaluator.setVariable("#TEXT", savedValue);
        }
    }
}
