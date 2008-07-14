package org.concordion.internal.command;

import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.*;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.Check;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class VerifyRowsCommand extends AbstractCommand {

    private Announcer<VerifyRowsListener> listeners = Announcer.to(VerifyRowsListener.class);
    private DocumentParser documentParser;

    public VerifyRowsCommand(DocumentParser documentParser) {

        this.documentParser = documentParser;
    }

    public void addVerifyRowsListener(VerifyRowsListener listener) {
        listeners.addListener(listener);
    }

    public void removeVerifyRowsListener(VerifyRowsListener listener) {
        listeners.removeListener(listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        // TODO: make less ugly
        String[] splitIterationExpression = evaluator.verifyIterationExpression(commandCall.getExpression());
        String loopVariableName = splitIterationExpression[0];
        String iterableExpression = splitIterationExpression[1];

        Object obj = evaluator.evaluate(iterableExpression);
        Check.notNull(obj, "Expression returned null (should be an Iterable).");
        Check.isTrue(obj instanceof Iterable, obj.getClass().getCanonicalName() + " is not Iterable");
        Check.isTrue(!(obj instanceof HashSet) || (obj instanceof LinkedHashSet), obj.getClass().getCanonicalName() + " does not have a predictable iteration order");
        Iterable<Object> iterable = (Iterable<Object>) obj;

        Strategy strategy;
        if (commandCall.getElement().isNamed("table")) {
            strategy = new TableStrategy();
        } else {
            strategy = new DefaultStrategy();
        }
        strategy.execute(commandCall, iterable, evaluator, loopVariableName, resultRecorder);
    }

    private void announceMissingRow(Element element) {
        listeners.announce().missingRow(new MissingRowEvent(element));
    }

    private void announceSurplusRow(Element element) {
        listeners.announce().surplusRow(new SurplusRowEvent(element));
    }

    private interface Strategy {
        void execute(CommandCall commandCall, Iterable<Object> iterable, Evaluator evaluator, String loopVariableName, ResultRecorder resultRecorder);
    }

    private class TableStrategy implements Strategy {
        public void execute(CommandCall commandCall, Iterable<Object> iterable, Evaluator evaluator, String loopVariableName, ResultRecorder resultRecorder) {
            TableSupport tableSupport = new TableSupport(commandCall, documentParser);

            Row[] detailRows = tableSupport.getDetailRows();

            int index = 0;
            for (Object loopVar : iterable) {
                evaluator.setVariable(loopVariableName, loopVar);
                Row detailRow;
                if (detailRows.length > index) {
                    detailRow = detailRows[index];
                } else {
                    detailRow = tableSupport.addDetailRow();
                    announceSurplusRow(detailRow.getElement());
                }
                //tableSupport.copyCommandCallsTo(detailRow);
                commandCall.setChildren(tableSupport.getCommandCallsFor(detailRow, commandCall.getResource()));
                commandCall.getChildren().setUp(evaluator, resultRecorder);
                commandCall.getChildren().execute(evaluator, resultRecorder);
                commandCall.getChildren().verify(evaluator, resultRecorder);
                index++;
            }

            for (; index < detailRows.length; index++) {
                Row detailRow = detailRows[index];
                resultRecorder.record(Result.FAILURE);
                announceMissingRow(detailRow.getElement());
            }
        }
    }

    private class DefaultStrategy implements Strategy {
        public void execute(CommandCall commandCall, Iterable<Object> iterable, Evaluator evaluator, String loopVariableName, ResultRecorder resultRecorder) {
            Element element = commandCall.getElement();

            Element parent = element.getParent();
            Element placeholder = new Element("span");
            parent.insertChildAfter(element, placeholder);

            Element lastSibling = placeholder;
            Element prototype = element.copy();
            parent.remove(element);

            for (Object loopVar : iterable) {
                Element newContent = prototype.copy();
                element.getParent().insertChildAfter(lastSibling, newContent);

                // TODO: fugly!
                CommandCall dummy = new CommandCall(VerifyRowsCommand.this, newContent, commandCall.getExpression(), commandCall.getResource());
                documentParser.generateCommandCallTree(newContent, dummy, commandCall.getResource());
                CommandCall duplicateOfThisVerifyRowsCommand = dummy.getChildren().get(0);
                CommandCallList children = duplicateOfThisVerifyRowsCommand.getChildren();

                evaluator.setVariable(loopVariableName, loopVar);
                children.processSequentially(evaluator, resultRecorder);

                lastSibling = newContent;
            }
            parent.remove(placeholder);
        }
    }
}
