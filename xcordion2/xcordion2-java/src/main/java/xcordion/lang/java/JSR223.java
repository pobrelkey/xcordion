package xcordion.lang.java;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import xcordion.api.EvaluationContext;
import xcordion.api.EvaluationContextFactory;
import xcordion.api.IgnoreState;
import xcordion.api.TestElement;
import xcordion.util.Coercions;
import xcordion.util.WrappingIterable;
import xcordion.util.XmlUtils;

public class JSR223 implements EvaluationContextFactory<JSR223.JSR223EvaluationContext> {

	public JSR223EvaluationContext newContext(String languageName, Object rootObject) {
		try {
			final ScriptEngine engine = new ScriptEngineManager().getEngineByName(languageName);
			if (engine != null) {
				return new JSR223EvaluationContext(engine, rootObject, new HashMap<String, Object>(), IgnoreState.NORMATIVE);
			}
			throw new ClassNotFoundException();  // keep compiler happy
		} catch (ClassNotFoundException e) {
			// not on Java 6 or JSR223 jar not in path - oh well
		}
		return null;
	}

	static public class JSR223EvaluationContext extends AbstractEvaluationContext<JSR223EvaluationContext> {

		private final ScriptEngine engine;
		private final Map<String, Object> values;

		private JSR223EvaluationContext(ScriptEngine engine, Object root, Map<String, Object> values, IgnoreState ignoreState) {
			super(root, ignoreState);
			this.engine = engine;
			this.values = new HashMap<String, Object>(values);
		}

		@Override
		protected Object doEval(String expression) {
			try {
				return engine.eval(expression, new SimpleBindings(values));
			} catch (ScriptException e) {
				// TODO
				throw new RuntimeException(e);
			}
		}

        final static private Pattern VARIABLE_NAME = Pattern.compile("^\\w+$");

		@Override
		protected boolean isOnlyVariableName(String expression) {
			return VARIABLE_NAME.matcher(expression).matches();
		}

		public Object getVariable(String name) {
			return values.get(name);
		}

		public void setVariable(String name, Object value) {
			values.put(name, value);
		}

		public JSR223EvaluationContext withIgnoreState(IgnoreState ignoreState) {
			return new JSR223EvaluationContext(engine, root, values, ignoreState);
		}

	}
}