package org.concordion.internal;

import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class CommandCallList extends ArrayList<CommandCall> {

    public void setUp(Evaluator evaluator, ResultRecorder resultRecorder) {
        for(CommandCall call : this) call.setUp(evaluator, resultRecorder);
    }
    
    public void execute(Evaluator evaluator, ResultRecorder resultRecorder) {
        for(CommandCall call : this) call.execute(evaluator, resultRecorder);
    }

    public void verify(Evaluator evaluator, ResultRecorder resultRecorder) {
        for(CommandCall call : this) call.verify(evaluator, resultRecorder);
    }

    public void processSequentially(Evaluator evaluator, ResultRecorder resultRecorder) {
        for(CommandCall call : this) {
            call.setUp(evaluator, resultRecorder);
            call.execute(evaluator, resultRecorder);
            call.verify(evaluator, resultRecorder);
        }
    }

}
