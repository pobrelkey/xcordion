package org.concordion.internal;

import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Evaluator;

import java.util.ArrayList;
import java.util.List;

public class OgnlValidatingEvaluator extends OgnlPermissiveEvaluator {

    final static private String[] BANNED_WORDS = new String[]{
        "click", "doubleClick", "enter", "open", "press", "type"
    };

    public OgnlValidatingEvaluator(Object fixture) {
        super(fixture);
    }

    @Override
    public Object evaluate(String expression) {
        validateEvaluationExpression(expression);
        checkForBannedWords(expression);
        return super.evaluate(expression);
    }

    @Override
    public void setVariable(String expression, Object value) {
        validateSetVariableExpression(expression);
        super.setVariable(expression, value);
    }

    private static String METHOD_NAME_PATTERN = "[a-z][a-zA-Z0-9_]*";
    private static String PROPERTY_NAME_PATTERN = "[a-z][a-zA-Z0-9_]*";
    private static String STRING_PATTERN = "'[^']+'";
    private static String LHS_VARIABLE_PATTERN = "#" + METHOD_NAME_PATTERN;
    private static String RHS_VARIABLE_PATTERN = "(" + LHS_VARIABLE_PATTERN + "|#TEXT|#VALUE|#HREF)";

    private static void checkForBannedWords(String expression) {
        for (String bannedWord : BANNED_WORDS) {
            if (expression.startsWith(bannedWord)) {
                throw new RuntimeException(
                          "Expression starts with a banned word ('" + bannedWord + "').\n"
                        + "This word strongly suggests you are writing a script.\n"
                        + "Concordion is a specification tool not a scripting tool.\n"
                        + "See the website http://www.concordion.org for more information.");
            }
        }
    }

    public static void validateEvaluationExpression(String expression) {

        // myProp
        // myMethod()
        // myMethod(#var1)
        // myMethod(#var1, #var2)
        // #var
        // #var.myProp
        // #var.myProp.myProp
        // #var = myProp
        // #var = myMethod()
        // #var = myMethod(#var1)
        // #var = myMethod(#var1, #var2)
        // #var ? 's1' : 's2'
        // myProp ? 's1' : 's2'
        // myMethod() ? 's1' : 's2'
        // myMethod(#var1) ? 's1' : 's2'
        // myMethod(#var1, #var2) ? 's1' : 's2'

        String METHOD_CALL_PARAMS = METHOD_NAME_PATTERN + " *\\( *" + RHS_VARIABLE_PATTERN + "(, *" + RHS_VARIABLE_PATTERN + " *)*\\)";
        String METHOD_CALL_NO_PARAMS = METHOD_NAME_PATTERN + " *\\( *\\)";
        String TERNARY_STRING_RESULT = " \\? " + STRING_PATTERN + " : " + STRING_PATTERN;

        List<String> regexs = new ArrayList<String>();
        regexs.add(PROPERTY_NAME_PATTERN);
        regexs.add(METHOD_CALL_NO_PARAMS);
        regexs.add(METHOD_CALL_PARAMS);
        regexs.add(RHS_VARIABLE_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + "\\." + PROPERTY_NAME_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + PROPERTY_NAME_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + METHOD_CALL_NO_PARAMS);
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + METHOD_CALL_PARAMS);
        regexs.add(LHS_VARIABLE_PATTERN + TERNARY_STRING_RESULT);
        regexs.add(PROPERTY_NAME_PATTERN + TERNARY_STRING_RESULT);
        regexs.add(METHOD_CALL_NO_PARAMS + TERNARY_STRING_RESULT);
        regexs.add(METHOD_CALL_PARAMS + TERNARY_STRING_RESULT);

        expression = expression.trim();
        for (String regex : regexs) {
            if (expression.matches(regex)) {
                return;
            }
        }
        throw new RuntimeException("Invalid expression [" + expression + "]");
    }

    public static void validateSetVariableExpression(String expression) {
        // #var                         VARIABLE
        // #var = myProp                VARIABLE = PROPERTY
        // #var = myMethod()            VARIABLE = METHOD
        // #var = myMethod(var1)        VARIABLE = METHOD_WITH_PARAM
        // #var = myMethod(var1, var2)  VARIABLE = METHOD_WITH_MULTIPLE_PARAMS

        List<String> regexs = new ArrayList<String>();
        regexs.add(RHS_VARIABLE_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + "\\." + PROPERTY_NAME_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + PROPERTY_NAME_PATTERN);
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + METHOD_NAME_PATTERN + " *\\( *\\)");
        regexs.add(LHS_VARIABLE_PATTERN + " *= *" + METHOD_NAME_PATTERN + " *\\( *" + RHS_VARIABLE_PATTERN + "(, *" + RHS_VARIABLE_PATTERN + " *)*\\)");

        expression = expression.trim();
        for (String regex : regexs) {
            if (expression.matches(regex)) {
                return;
            }
        }
        throw new RuntimeException("Invalid 'set' expression [" + expression + "]");
    }

    static public class Factory implements EvaluatorFactory {
        public Evaluator createEvaluator(Object fixture) {
            return new OgnlValidatingEvaluator(fixture);
        }
    }

}
