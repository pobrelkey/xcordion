package xcordion.lang.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import xcordion.api.EvaluationContext;
import xcordion.api.EvaluationContextFactory;
import xcordion.api.IgnoreState;
import xcordion.api.TestElement;
import xcordion.util.Coercions;
import xcordion.util.WrappingIterable;
import xcordion.util.XmlUtils;

public class BeanScriptingFramework implements EvaluationContextFactory<BeanScriptingFramework.BSFEvaluationContext> {

	public BSFEvaluationContext newContext(String languageName, Object rootObject) {
		try {
			BSFEngine engine = null;
			try {
				engine = new BSFManager().loadScriptingEngine(languageName);
			} catch (BSFException e) {
				if (e.getReason() != BSFException.REASON_UNKNOWN_LANGUAGE) {
					throw new RuntimeException(e);
				}
			}
			if (engine != null) {
				return new BSFEvaluationContext(engine, rootObject, new HashMap<String, Object>(), IgnoreState.NORMATIVE);
			}
			throw new ClassNotFoundException();  // keep compiler happy
		} catch (ClassNotFoundException e) {
			// BSF not present - oh well
		}
		return null;
	}

	static public class BSFEvaluationContext extends AbstractEvaluationContext<BSFEvaluationContext> {

		private final BSFEngine engine;
		private final Map<String, Object> values;

		private BSFEvaluationContext(BSFEngine engine, Object root, Map<String, Object> values, IgnoreState ignoreState) {
			super(root, ignoreState);
			this.engine = engine;
			this.values = new HashMap<String, Object>(values);
		}

		@Override
		protected Object doEval(String expression) {
			Vector<String> paramNames = new Vector<String>();
			Vector<Object> paramValues = new Vector<Object>();
			for (Map.Entry<String, Object> entry : values.entrySet()) {
				paramNames.add(entry.getKey());
				paramValues.add(entry.getValue());
			}
			try {
				return engine.apply("<xcordion expression>", 0, 0, expression, paramNames, paramValues);
			} catch (BSFException ex) {
				throw new RuntimeException(ex);
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

		public BSFEvaluationContext withIgnoreState(IgnoreState ignoreState) {
			return new BSFEvaluationContext(engine, root, values, ignoreState);
		}

	}
}