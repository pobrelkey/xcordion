using System;
using System.Collections.Generic;
using System.Text;
using xcordion.api;
using xcordion.util;
using ognl;
using System.Text.RegularExpressions;

namespace xcordion.lang.csharp
{
    public class OgnlEvaluationContext : EvaluationContext<OgnlEvaluationContext>
    {
        private const string SPECIALVARIABLE_TEXT = "TEXT";
        private const string SPECIALVARIABLE_VALUE = "VALUE";
        private const string SPECIALVARIABLE_HREF = "HREF";


        private Object root;
        private OgnlContext context;

        public OgnlEvaluationContext(Object rootObject)
            : this(rootObject, new OgnlContext(new SimpleMap<object, object>()))
        {
        }

        private OgnlEvaluationContext(Object rootObject, OgnlContext ognlContext)
        {
            this.root = rootObject;
            this.context = ognlContext;
        }

        public OgnlEvaluationContext subContext()
        {
            return new OgnlEvaluationContext(root, new OgnlContext(context));
        }

        public object eval<T>(string expression, T element) where T : class, TestElement<T>
        {
            object savedText = null, savedValue = null, savedHref = null;
            bool hasText = expression.Contains(SPECIALVARIABLE_TEXT),
                    hasValue = expression.Contains(SPECIALVARIABLE_VALUE),
                    hasHref = expression.Contains(SPECIALVARIABLE_HREF);
            if (hasText)
            {
                savedText = getVariable(SPECIALVARIABLE_TEXT);
                setVariable(SPECIALVARIABLE_TEXT, element.getValue());
            }
            if (hasValue)
            {
                savedValue = getVariable(SPECIALVARIABLE_VALUE);
                setVariable(SPECIALVARIABLE_VALUE, getValue(element, null));
            }
            if (hasHref)
            {
                savedValue = getVariable(SPECIALVARIABLE_HREF);
                setVariable(SPECIALVARIABLE_HREF, getFirstChildHref(element));
            }


            try
            {
                return Ognl.getValue(expression, context, root);
            }
            finally
            {
                if (hasText)
                {
                    setVariable(SPECIALVARIABLE_TEXT, savedText);
                }
                if (hasValue)
                {
                    setVariable(SPECIALVARIABLE_VALUE, savedValue);
                }
                if (hasHref)
                {
                    setVariable(SPECIALVARIABLE_HREF, savedHref);
                }
            }
        }

        private string getFirstChildHref<T>(T element) where T : class, TestElement<T> {
            string href = element.getAttribute("href");
            if (href != null) 
            {
                return href;
            }
            foreach (T child in element.getChildren()) 
            {
                href = getFirstChildHref(child);
                if (href != null) 
                {
                    return href;
                }
            }
            return null;
        }

        private static readonly Regex FOREIGN_CHARACTERS = new Regex("[^\\w#]");

        public Object set<T>(string expression, T element) where T : class, TestElement<T>
        {
            object value = getValue(element, null);
            expression = expression.Trim();

            if (!FOREIGN_CHARACTERS.IsMatch(expression))
            {
                if (expression[0] == '#')
                {
                    expression = expression.Substring(1);
                }
                setVariable(expression, value);
            }
            else
            {
                eval(expression, element);
            }
            return value;
        }

        public Iterable<OgnlEvaluationContext> iterate<T>(string expression, T element) where T : class, TestElement<T>
        {
			string variable, collectionExpression;
			try {
				int colon = expression.IndexOf(':');
				variable = expression.Substring(0, colon).Trim();
				collectionExpression = expression.Substring(colon + 1).Trim();
				if (variable[0] == '#') 
                {
					variable = variable.Substring(1);
				}
			} catch (Exception) {
				// TODO
				throw new ParseException("malformed iteration expression: " + expression);
			}

            return new WrappingIterable<object, OgnlEvaluationContext>(
                Coercions.toEnumerable(eval(collectionExpression, element)),
                new SubContextFactory(this, variable).wrap);
        }

        public object getVariable(string name)
        {
            return context[name];
        }

        public void setVariable(string name, object value)
        {
            if (context.Contains(name))
            {
                context.Remove(name);
            }
            context[name] = value;
        }

        public object getValue<T>(T element, Type asClass) where T : class, TestElement<T>
        {
            object result = element.getValue();
            return (asClass != null) ? Convert.ChangeType(result, asClass) : result;
        }
    }

    internal class SubContextFactory
    {
        private string variable;
        private OgnlEvaluationContext parentContext;

        internal SubContextFactory(OgnlEvaluationContext parentContext, string variable)
        {
            this.variable = variable;
            this.parentContext = parentContext;
        }

        public OgnlEvaluationContext wrap(object raw) 
        {
            OgnlEvaluationContext result = parentContext.subContext();
            result.setVariable(variable, raw);
            return result;            
        }        
    }  
}
