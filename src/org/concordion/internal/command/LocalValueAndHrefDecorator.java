package org.concordion.internal.command;

import org.concordion.internal.CommandCall;
import org.concordion.internal.util.XmlFunctional;
import static org.concordion.internal.util.XmlFunctional.*;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

public class LocalValueAndHrefDecorator extends AbstractCommandDecorator {

    @Override
    protected void process(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder, Runnable runnable) {
        Object savedValue = evaluator.getVariable("VALUE");
        Object savedHref = evaluator.getVariable("HREF");
        String href = null;
        try {
            evaluator.setVariable("#VALUE", commandCall.getElement().getText());
            href = getElementOrChildsAttributeValue(commandCall.getElement(), "href");
            if (href != null) {
                evaluator.setVariable("#HREF", href);
            }
            runnable.run();
        } finally {
            evaluator.setVariable("#VALUE", savedValue);
            if (href != null) {
                evaluator.setVariable("#HREF", savedHref);
            }
        }
    }

}
