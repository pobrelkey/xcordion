package xcordion.impl.theme;

import xcordion.api.XcordionEvents;
import xcordion.api.*;
import xcordion.util.Coercions;

import java.util.ArrayList;
import java.util.List;


// TODO: loads of original concordion code here
public class ConcordionMarkupTheme<T extends TestElement<T>> implements XcordionEvents<T> {

    private static final String RESOURCE_TOGGLESCRIPT = "/xcordion/impl/theme/visibility-toggler.js";
    private static final String RESOURCE_STYLESHEET = "/xcordion/impl/theme/embedded.css";
    private static final String RESOURCE_LOGO = "/xcordion/impl/theme/logo.png";
    
    private int buttonId;
    private ArrayList<ResourceReference<T>> resourceReferences = new ArrayList<ResourceReference<T>>();
    private boolean hasToggleScript;

    public void begin(T target) {
        buttonId = 0;
        resourceReferences.clear();
        hasToggleScript = false;

        T stylesheetElement = getHeadElement(target).addChild("link");
        stylesheetElement.setAttribute("rel", "stylesheet");
        stylesheetElement.setAttribute("type", "text/css");
        resourceReferences.add(new ResourceReference<T>(stylesheetElement, "href", RESOURCE_STYLESHEET));
    }

    public void succesfulSet(T target, String expression, Object value) {
        target.addStyleClass("processed");
    }

    public void exception(T target, String expression, Throwable e) {
        buttonId++;

        target.appendChild(expectedSpan(target));
        target.appendChild(exceptionMessage(target.getDocument(), Coercions.toExceptionMessage(e)));
        target.appendChild(stackTraceTogglingButton(target.getDocument()));
        target.appendChild(stackTrace(target.getDocument(), e, expression));

        if (!hasToggleScript) {
            T scriptElement = getHeadElement(target).addChild("script");
            scriptElement.setAttribute("type", "text/javascript");
            scriptElement.setText(" ");
            resourceReferences.add(new ResourceReference<T>(scriptElement, "src", RESOURCE_TOGGLESCRIPT));
            hasToggleScript = true;
        }
    }

    public void succesfulExecute(T target, String expression) {
        target.addStyleClass("processed");
    }

    public void insertText(T target, String expression, Object result) {
        target.setText((result != null) ? result.toString() : "(null)");
        target.addStyleClass("processed");
    }

    public void successfulAssertBoolean(T target, String expression, boolean value) {
        target.addStyleClass("success").appendNonBreakingSpaceIfBlank();
    }

    public void failedAssertBoolean(T target, String expression, boolean expected, Object actual) {
        target.addStyleClass("failure");

        T spanExpected = target.getDocument().newElement(null, "del");
        spanExpected.addStyleClass("expected");
        target.moveContentTo(spanExpected);
        spanExpected.appendNonBreakingSpaceIfBlank();

        T spanActual = target.getDocument().newElement(null, "ins");
        spanActual.addStyleClass("actual");
        spanActual.appendText("== " + convertToString(actual));
        spanActual.appendNonBreakingSpaceIfBlank();

        target.appendChild(spanExpected);
        target.appendChild(spanActual);
    }

    public void successfulAssertEquals(T target, String expression, Object expected) {
        target.addStyleClass("success").appendNonBreakingSpaceIfBlank();
    }

    public void failedAssertEquals(T target, String expression, Object expected, Object actual) {
        target.addStyleClass("failure");

        T spanExpected = target.getDocument().newElement(null, "del");
        spanExpected.addStyleClass("expected");
        target.moveContentTo(spanExpected);
        target.appendChild(spanExpected);
        spanExpected.appendNonBreakingSpaceIfBlank();

        T spanActual = target.getDocument().newElement(null, "ins");
        spanActual.addStyleClass("actual");
        spanActual.appendText(convertToString(actual));
        spanActual.appendNonBreakingSpaceIfBlank();

        target.appendText("\n");
        target.appendChild(spanActual);
    }

    public void successfulAssertContains(T target, String expression, Object expected, Object actual) {
        successfulAssertEquals(target, expression, expected);
    }

    public void failedAssertContains(T target, String expression, Object expected, Object actual) {
        failedAssertEquals(target, expression, expected, actual);
    }

    public void missingRow(RowNavigator<T> row) {
        row.getRowElement().addStyleClass("missing");
    }

    public void surplusRow(RowNavigator<T> row) {
        row.getRowElement().addStyleClass("surplus");
    }

    public void end(T target) {
        // TODO: append stats?
    }

    private T getHeadElement(T target) {
        T headElement = target.getDocument().getRootElement().getFirstChildNamed("head");
        if (headElement == null) {
            // <head> element is missing - make do with root element...
            headElement = target.getDocument().getRootElement();
        }
        return headElement;
    }


    private String convertToString(Object actual) {
        return (actual != null) ? actual.toString() : "(null)";
    }

    private T expectedSpan(T element) {
        T spanExpected = element.getDocument().newElement("del").addStyleClass("expected");
        element.moveContentTo(spanExpected);
        spanExpected.appendNonBreakingSpaceIfBlank();
        T spanFailure = element.getDocument().newElement("span").addStyleClass("failure");
        spanFailure.appendChild(spanExpected);
        return spanFailure;
    }

    private T exceptionMessage(TestDocument<T> document, String exceptionMessage) {
        return document.newElement("span")
                .addStyleClass("exceptionMessage")
                .appendText(exceptionMessage);
    }

    private T  stackTraceTogglingButton(TestDocument<T> document) {
        return document.newElement("input")
                .addStyleClass("stackTraceButton")
                .setAttribute("id", "stackTraceButton" + buttonId)
                .setAttribute("type", "button")
                .setAttribute("onclick", "javascript:toggleStackTrace('" + buttonId + "')")
                .setAttribute("value", "View Stack");
    }

    private T stackTrace(TestDocument<T> document, Throwable t, String expression) {
        T stackTrace = document.newElement("span").addStyleClass("stackTrace");
        stackTrace.setAttribute("id", "stackTrace" + buttonId);

        if (expression != null) {
            T p = document.newElement("p").setText("While evaluating expression: ");
            p.appendChild(document.newElement("code").setText(expression));
            stackTrace.appendChild(p);
        }

        stackTrace.appendText(Coercions.toStackTrace(t));

        return stackTrace;
    }


    public List<ResourceReference<T>> getResourceReferences() {
        return resourceReferences;
    }
}



