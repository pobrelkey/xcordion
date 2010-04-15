package xcordion.lang.java;

import xcordion.api.EvaluationContext;
import xcordion.api.TestElement;
import xcordion.api.IgnoreState;
import xcordion.util.Coercions;
import xcordion.util.WrappingIterable;
import xcordion.util.XmlUtils;

public abstract class AbstractEvaluationContext<K extends AbstractEvaluationContext<K>> implements EvaluationContext<K> {

	private static final String SPECIALVARIABLE_TEXT = "TEXT";
	private static final String SPECIALVARIABLE_VALUE = "VALUE";
	private static final String SPECIALVARIABLE_HREF = "HREF";

	protected final Object root;
	protected final IgnoreState ignoreState;

	protected AbstractEvaluationContext(Object root, IgnoreState ignoreState) {
		this.root = root;
		this.ignoreState = ignoreState;
	}

	public <T extends TestElement<T>> Object eval(String expression, T element) {
		Object savedText = null, savedValue = null, savedHref = null;
		boolean hasText = expression.indexOf(SPECIALVARIABLE_TEXT) != -1,
				hasValue = expression.indexOf(SPECIALVARIABLE_VALUE) != -1,
				hasHref = expression.indexOf(SPECIALVARIABLE_HREF) != -1;
		if (hasText) {
			savedText = getVariable(SPECIALVARIABLE_TEXT);
			setVariable(SPECIALVARIABLE_TEXT, element.getValue());
		}
		if (hasValue) {
			savedValue = getVariable(SPECIALVARIABLE_VALUE);
			setVariable(SPECIALVARIABLE_VALUE, getValue(element, null));
		}
		if (hasHref) {
			savedHref = getVariable(SPECIALVARIABLE_HREF);
			setVariable(SPECIALVARIABLE_HREF, XmlUtils.getFirstChildHref(element));
		}

		try {
			return doEval(expression);
		} finally {
			if (hasText) {
				setVariable(SPECIALVARIABLE_TEXT, savedText);
			}
			if (hasValue) {
				setVariable(SPECIALVARIABLE_VALUE, savedValue);
			}
			if (hasHref) {
				setVariable(SPECIALVARIABLE_HREF, savedHref);
			}
		}
	}

	public <T extends TestElement<T>> Iterable<K> iterate(String expression, T element) {
		String variable, collectionExpression;
		try {
			int colon = expression.indexOf(':');
			variable = expression.substring(0, colon).trim();
			collectionExpression = expression.substring(colon + 1).trim();
			variable = mungeVariableName(variable);
		} catch (StringIndexOutOfBoundsException e) {
			// TODO
			throw new RuntimeException("malformed iteration expression: " + expression);
		}

		final Iterable collection = Coercions.toIterable(eval(collectionExpression, element));
		final String variableName  = variable;

		return new WrappingIterable<Object, K>(collection) {
			protected K wrap(Object base) {
				K result = subContext();
				result.setVariable(variableName, base);
				return result;
			}
		};
	}

	public <T extends TestElement<T>> Object set(String expression, T element) {
		Object value = getValue(element, null);
		expression = expression.trim();

		if (isOnlyVariableName(expression)) {
			setVariable(expression, value);
		} else {
			eval(expression, element);
		}
		return value;
	}

	public K subContext() {
		return withIgnoreState(ignoreState);
	}

	public IgnoreState getIgnoreState() {
		return ignoreState;
	}

	public <T extends TestElement<T>> Object getValue(T element, Class asClass) {
		return XmlUtils.elementToValue(element, asClass);
	}

	protected String mungeVariableName(String variableName) {
		return variableName;
	}

	abstract protected Object doEval(String expression);
	abstract protected boolean isOnlyVariableName(String expression);


}
