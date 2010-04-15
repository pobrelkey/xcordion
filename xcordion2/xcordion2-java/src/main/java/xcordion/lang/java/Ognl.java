package xcordion.lang.java;

import ognl.OgnlContext;
import ognl.OgnlException;
import xcordion.api.EvaluationContext;
import xcordion.api.EvaluationContextFactory;
import xcordion.api.TestElement;
import xcordion.api.IgnoreState;
import xcordion.util.Coercions;
import xcordion.util.WrappingIterable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;
import xcordion.util.XmlUtils;

public class Ognl implements EvaluationContextFactory<Ognl.OgnlEvaluationContext> {

	public OgnlEvaluationContext newContext(String languageName, Object rootObject) {
		if (!languageName.equalsIgnoreCase("ognl")) {
			return null;
		}

		return new OgnlEvaluationContext(rootObject);
	}

	static public class OgnlEvaluationContext extends AbstractEvaluationContext<OgnlEvaluationContext> {

		private OgnlContext context;

        public OgnlEvaluationContext(Object rootObject) {
			this(rootObject, new OgnlContext(), IgnoreState.NORMATIVE);
		}

		private OgnlEvaluationContext(Object rootObject, OgnlContext ognlContext, IgnoreState ignoreState) {
			super(rootObject, ignoreState);
			this.context = ognlContext;
		}

		public Object getVariable(String name) {
			return context.get(name);
		}

		public void setVariable(String name, Object value) {
			context.put(name, value);
		}

        public OgnlEvaluationContext withIgnoreState(IgnoreState ignoreState) {
            return new OgnlEvaluationContext(root, new OgnlContext(context), ignoreState);
        }

        final static private Pattern VARIABLE_NAME = Pattern.compile("^#?\\w+$");

		protected boolean isOnlyVariableName(String expression) {
			return VARIABLE_NAME.matcher(expression).matches();
		}

		protected String mungeVariableName(String variable) {
			if (variable.charAt(0) == '#') {
				variable = variable.substring(1);
			}
			return variable;
		}

		protected Object doEval(String expression) {
			try {
				return ognl.Ognl.getValue(expression, context, root);
			} catch (OgnlException e) {
				// TODO
				throw new RuntimeException(e);
			}
		}

	}

}
