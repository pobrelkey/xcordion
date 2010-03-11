package xcordion.impl.theme;

import xcordion.api.IgnoreState;
import xcordion.api.ResourceReference;
import xcordion.api.TestDocument;
import xcordion.api.TestElement;
import xcordion.api.XcordionEventListener;
import xcordion.api.events.BeginEvent;
import xcordion.api.events.ChangedIgnoreStateEvent;
import xcordion.api.events.EndEvent;
import xcordion.api.events.ExceptionThrownEvent;
import xcordion.api.events.FailedAssertBooleanEvent;
import xcordion.api.events.FailedAssertContainsEvent;
import xcordion.api.events.FailedAssertEqualsEvent;
import xcordion.api.events.InsertTextEvent;
import xcordion.api.events.MissingRowEvent;
import xcordion.api.events.SuccessfulAssertBooleanEvent;
import xcordion.api.events.SuccessfulAssertContainsEvent;
import xcordion.api.events.SuccessfulAssertEqualsEvent;
import xcordion.api.events.SuccessfulExecuteEvent;
import xcordion.api.events.SuccessfulSetEvent;
import xcordion.api.events.SurplusRowEvent;
import xcordion.api.events.XcordionEvent;
import xcordion.util.Coercions;

import java.util.ArrayList;
import java.util.List;


// TODO: loads of original concordion code here
public class ConcordionMarkupTheme<T extends TestElement<T>> implements XcordionEventListener<T> {

    private static final String RESOURCE_TOGGLESCRIPT = "/xcordion/impl/theme/visibility-toggler.js";
    private static final String RESOURCE_STYLESHEET = "/xcordion/impl/theme/embedded.css";
    
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

        e = Coercions.toDisplayableException(e);
        
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

    public void missingRow(T rowElement) {
        rowElement.addStyleClass("missing");
    }

    public void surplusRow(T rowElement) {
        rowElement.addStyleClass("surplus");
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

    public void changedIgnoreState(T target, IgnoreState ignoreState) {
        if (ignoreState == IgnoreState.NORMATIVE) {
            target.addStyleClass("normative");
        } else if (ignoreState == IgnoreState.IGNORED) {
            target.addStyleClass("ignored");
        } else if (ignoreState == IgnoreState.OMITTED) {
            target.addStyleClass("omitted");
        }
    }

    public void handleEvent(XcordionEvent<T> xcordionEvent) {
        if (xcordionEvent instanceof BeginEvent) {
            begin(xcordionEvent.getElement());
        } else if (xcordionEvent instanceof ChangedIgnoreStateEvent) {
            changedIgnoreState(xcordionEvent.getElement(), xcordionEvent.getIgnoreState());
        } else if (xcordionEvent instanceof EndEvent) {
            end(xcordionEvent.getElement());
        } else if (xcordionEvent instanceof ExceptionThrownEvent) {
            ExceptionThrownEvent<T> e = (ExceptionThrownEvent<T>) xcordionEvent;
            exception(e.getElement(), e.getExpression(), e.getThrowable());
        } else if (xcordionEvent instanceof FailedAssertBooleanEvent) {
            FailedAssertBooleanEvent<T> e = (FailedAssertBooleanEvent<T>) xcordionEvent;
            failedAssertBoolean(e.getElement(), e.getExpression(), e.getExpected(), e.getActual());
        } else if (xcordionEvent instanceof FailedAssertContainsEvent) {
            FailedAssertContainsEvent<T> e = (FailedAssertContainsEvent<T>) xcordionEvent;
            failedAssertContains(e.getElement(), e.getExpression(), e.getExpected(), e.getActual());
        } else if (xcordionEvent instanceof FailedAssertEqualsEvent) {
            FailedAssertEqualsEvent<T> e = (FailedAssertEqualsEvent<T>) xcordionEvent;
            failedAssertEquals(e.getElement(), e.getExpression(), e.getExpected(), e.getActual());
        } else if (xcordionEvent instanceof InsertTextEvent) {
            InsertTextEvent<T> e = (InsertTextEvent<T>) xcordionEvent;
            insertText(e.getElement(), e.getExpression(), e.getResult());
        } else if (xcordionEvent instanceof MissingRowEvent) {
            missingRow(xcordionEvent.getElement());
        } else if (xcordionEvent instanceof SuccessfulAssertBooleanEvent) {
            SuccessfulAssertBooleanEvent<T> e = (SuccessfulAssertBooleanEvent<T>) xcordionEvent;
            successfulAssertBoolean(e.getElement(), e.getExpression(), e.getExpected());
        } else if (xcordionEvent instanceof SuccessfulAssertContainsEvent) {
            SuccessfulAssertContainsEvent<T> e = (SuccessfulAssertContainsEvent<T>) xcordionEvent;
            successfulAssertContains(e.getElement(), e.getExpression(), e.getExpected(), e.getActual());
        } else if (xcordionEvent instanceof SuccessfulAssertEqualsEvent) {
            SuccessfulAssertEqualsEvent<T> e = (SuccessfulAssertEqualsEvent<T>) xcordionEvent;
            successfulAssertEquals(e.getElement(), e.getExpression(), e.getExpected());
        } else if (xcordionEvent instanceof SuccessfulExecuteEvent) {
            SuccessfulExecuteEvent<T> e = (SuccessfulExecuteEvent<T>) xcordionEvent;
            succesfulExecute(e.getElement(), e.getExpression());
        } else if (xcordionEvent instanceof SuccessfulSetEvent) {
            SuccessfulSetEvent<T> e = (SuccessfulSetEvent<T>) xcordionEvent;
            succesfulSet(e.getElement(), e.getExpression(), e.getValue());
        } else if (xcordionEvent instanceof SurplusRowEvent) {
            surplusRow(xcordionEvent.getElement());
        }
    }
}



