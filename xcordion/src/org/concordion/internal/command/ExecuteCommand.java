package org.concordion.internal.command;

import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.*;

public class ExecuteCommand extends AbstractCommand {

    private DocumentParser documentParser;

    public ExecuteCommand(DocumentParser documentParser) {
        this.documentParser = documentParser;
    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Strategy strategy;
        if (commandCall.getElement().isNamed("table")) {
            strategy = new TableStrategy();
        } else {
            strategy = new DefaultStrategy();
        }
        strategy.execute(commandCall, evaluator, resultRecorder);
    }
    
    private interface Strategy {
        void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);
    }
    
    private class DefaultStrategy implements Strategy {

        public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            CommandCallList childCommands = commandCall.getChildren();
            
            childCommands.setUp(evaluator, resultRecorder);
            evaluator.evaluate(commandCall.getExpression());
            childCommands.execute(evaluator, resultRecorder);
            childCommands.verify(evaluator, resultRecorder);
        }
    }
    
    private class TableStrategy implements Strategy {

        public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            TableSupport tableSupport = new TableSupport(commandCall, documentParser);
            Row[] detailRows = tableSupport.getDetailRows();
            int rownum = 0;
            Object oldRowNum = evaluator.getVariable("ROWNUM");
            for (Row detailRow : detailRows) {
                commandCall.setElement(detailRow.getElement());
                //tableSupport.copyCommandCallsTo(detailRow);
                commandCall.setChildren(tableSupport.getCommandCallsFor(detailRow, commandCall.getResource()));
                evaluator.setVariable("#ROWNUM", rownum++);
                commandCall.execute(evaluator, resultRecorder);
            }
            evaluator.setVariable("#ROWNUM", oldRowNum);
        }
        
        
    
    }
    
}
