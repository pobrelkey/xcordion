package org.concordion.internal.listener;

import org.concordion.internal.command.AssertBooleanListener;
import org.concordion.internal.command.AssertBooleanSuccessEvent;
import org.concordion.internal.command.AssertBooleanFailureEvent;
import org.concordion.api.Element;

public class AssertBooleanResultRenderer implements AssertBooleanListener {
    public void successReported(AssertBooleanSuccessEvent event) {
        event.getElement()
            .addStyleClass("success")
            .appendNonBreakingSpaceIfBlank();
    }

    public void failureReported(AssertBooleanFailureEvent event) {
        Element element = event.getElement();
        element.addStyleClass("failure");

        Element spanExpected = new Element("del");
        spanExpected.addStyleClass("expected");
        element.moveChildrenTo(spanExpected);
        element.appendChild(spanExpected);
        spanExpected.appendNonBreakingSpaceIfBlank();

        Element spanActual = new Element("ins");
        spanActual.addStyleClass("actual");
        spanActual.appendText(event.getExpression());
        spanActual.appendNonBreakingSpaceIfBlank();

        Element kicker = new Element("b");
        kicker.appendText(" != " + event.getExpected());
        spanActual.appendChild(kicker);

        element.appendText("\n");
        element.appendChild(spanActual);

    }
}
