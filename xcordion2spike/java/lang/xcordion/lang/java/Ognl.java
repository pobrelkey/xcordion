package xcordion.lang.java;

import ognl.OgnlContext;
import ognl.OgnlException;
import xcordion.api.EvaluationContext;
import xcordion.api.EvaluationContextFactory;
import xcordion.api.TestElement;
import xcordion.util.Coercions;
import xcordion.util.WrappingIterable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

public class Ognl implements EvaluationContextFactory<Ognl.OgnlEvaluationContext> {

	public OgnlEvaluationContext newContext(String languageName, Object rootObject) {
		if (!languageName.equalsIgnoreCase("ognl")) {
			return null;
		}

		return new OgnlEvaluationContext(rootObject);
	}

	static public class OgnlEvaluationContext implements EvaluationContext<OgnlEvaluationContext> {

        private static final String SPECIALVARIABLE_TEXT = "TEXT";
        private static final String SPECIALVARIABLE_VALUE = "VALUE";
        private static final String SPECIALVARIABLE_HREF = "HREF";

		private Object root;
		private OgnlContext context;

        public OgnlEvaluationContext(Object rootObject) {
			this(rootObject, new OgnlContext());
		}

		private OgnlEvaluationContext(Object rootObject, OgnlContext ognlContext) {
			this.root = rootObject;
			this.context = ognlContext;
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
                savedValue = getVariable(SPECIALVARIABLE_HREF);
                setVariable(SPECIALVARIABLE_HREF, getFirstChildHref(element));
            }

            try {
                return ognl.Ognl.getValue(expression, context, root);
			} catch (OgnlException e) {
				// TODO
				throw new RuntimeException(e);
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

        private <T extends TestElement<T>> String getFirstChildHref(T element) {
            String href = element.getAttribute("href");
            if (href != null) {
                return href;
            }
            for (T child : element.getChildren()) {
                href = getFirstChildHref(child);
                if (href != null) {
                    return href;
                }
            }
            return null;
        }

        public <T extends TestElement<T>> Iterable<OgnlEvaluationContext> iterate(String expression, T element) {
			String variable, collectionExpression;
			try {
				int colon = expression.indexOf(':');
				variable = expression.substring(0, colon).trim();
				collectionExpression = expression.substring(colon + 1).trim();
				if (variable.charAt(0) == '#') {
					variable = variable.substring(1);
				}
			} catch (StringIndexOutOfBoundsException e) {
				// TODO
				throw new RuntimeException("malformed iteration expression: " + expression);
			}

			final Iterable collection = Coercions.toIterable(eval(collectionExpression, element));
			final String variableName  = variable;

			return new WrappingIterable<Object, OgnlEvaluationContext>(collection) {
				protected OgnlEvaluationContext wrap(Object base) {
					OgnlEvaluationContext result = subContext();
					result.setVariable(variableName, base);
					return result;
				}
			};
		}

        final static private Pattern FOREIGN_CHARACTERS = Pattern.compile("[^\\w#]");

        public <T extends TestElement<T>> Object set(String expression, T element) {
            Object value = getValue(element, null);
			expression = expression.trim();

            if (!FOREIGN_CHARACTERS.matcher(expression).matches()) {
				if (expression.charAt(0) == '#') {
					expression = expression.substring(1);
				}
				context.put(expression, value);
			} else {
				eval(expression, element);
			}
            return value;
        }

		public OgnlEvaluationContext subContext() {
			return new OgnlEvaluationContext(root, new OgnlContext(context));
		}

		public Object getVariable(String name) {
			return context.get(name);
		}

		public void setVariable(String name, Object value) {
			context.put(name, value);
		}

        public <T extends TestElement<T>> Object getValue(T element, Class asClass) {
            Object value = element.getValue();
            if (value != null && asClass != null && !value.getClass().isAssignableFrom(asClass)) {
                if (asClass.isAssignableFrom(String.class)) {
                    return value.toString();
                }
                
                // does target class have a one-arg constructor taking an argument assignable from value?
                // failing that, does target class have a one-arg constructor taking a String?
                Constructor bestOneArg = null, stringOneArg = null;
                for (Constructor c : asClass.getConstructors()) {
                    Class[] paramClasses = c.getParameterTypes();
                    if (paramClasses.length != 1) {
                        continue;
                    }
                    if (paramClasses[0].equals(value.getClass())) {
                        bestOneArg = c;
                        break;
                    } else if (paramClasses[0].isAssignableFrom(value.getClass()) &&
                            (bestOneArg == null || !bestOneArg.getParameterTypes()[0].isAssignableFrom(paramClasses[0]))) {
                        bestOneArg = c;
                    } else if (paramClasses[0].isAssignableFrom(String.class)) {
                        stringOneArg = c;
                    }
                }
                try {
                    if (bestOneArg != null) {
                        return bestOneArg.newInstance(value);
                    } else if (stringOneArg != null) {
                        return stringOneArg.newInstance(value.toString());
                    }
                } catch (InstantiationException e) {
                    // TODO
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    // TODO
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    // TODO
                    throw new RuntimeException(e);
                }
            }
            return element.getValue();
        }

    }

}
