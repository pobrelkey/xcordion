package org.concordion.internal;

import ognl.MethodFailedException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.concordion.api.Evaluator;
import org.concordion.api.EvaluatorFactory;
import org.concordion.internal.util.Check;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class OgnlPermissiveEvaluator implements Evaluator {

    private Object rootObject;
    private final OgnlContext ognlContext = new OgnlContext();


    public OgnlPermissiveEvaluator() {
    }

    public OgnlPermissiveEvaluator(Object rootObject) {
        this.rootObject = rootObject;
    }

    public void setRootObject(Object rootObject) {
        this.rootObject = rootObject;
    }

    public Object evaluate(String expression) {
        Check.notNull(rootObject, "Root object is null");
        Check.notNull(expression, "Expression to evaluate cannot be null");
        try {
            return Ognl.getValue(expression, ognlContext, rootObject);
        } catch (OgnlException e) {
            throw invalidExpressionException(e);
        }
    }

    public String[] verifyIterationExpression(String expression) {
        Pattern pattern = Pattern.compile("(#.+?) *: *(.+)");
        Matcher matcher = pattern.matcher(expression);
        if (!matcher.matches()) {
            throw new RuntimeException("The expression for a \"verifyRows\" should be of the form: #var : collectionExpr");
        }
        return new String[]{matcher.group(1), matcher.group(2)};
    }

    private InvalidExpressionException invalidExpressionException(OgnlException e) {
        Throwable cause = e;
        
        String message = e.getMessage();
        if (e.getReason() != null) {
            message = e.getReason().getMessage();
            cause = e.getReason();
        }
        if (message == null) {
            message = "";
        }
        if (e instanceof MethodFailedException) {
            MethodFailedException ex = ((MethodFailedException) e);
            Throwable realReason = ex.getReason();
            if (realReason != null) {
                if (realReason instanceof NullPointerException) {
                    message = "NullPointerException";
                } else {
                    message = realReason.getClass().getName() + ": " + message;
                }
            }
            message = message.replaceAll("java\\.lang\\.", "");
        }
        return new InvalidExpressionException(message, cause);
    }

    public void setVariable(String expression, Object value) {
        if (expression.contains("=") || expression.contains(",") || expression.contains("(")) {
            evaluate(expression);
        } else if (expression.startsWith("#")) {
            putVariable(expression.substring(1), value);
        } else {
            putVariable(expression, value);
        }
    }

    private void putVariable(String variableName, Object value) {
        Check.isFalse(variableName.startsWith("#"), "Variable name passed to evaluator should not start with #");
        Check.isTrue(!variableName.equals("in"), "'%s' is a reserved word and cannot be used for variables names", variableName);
        ognlContext.put(variableName, value);
    }

    public Object getVariable(String variableName) {
        if (variableName.startsWith("#")) {
            variableName = variableName.substring(1);
        }
        return ognlContext.get(variableName);
    }

    static public class Factory implements EvaluatorFactory {
        public Evaluator createEvaluator(Object fixture) {
            return new OgnlPermissiveEvaluator(fixture);
        }
    }


}
