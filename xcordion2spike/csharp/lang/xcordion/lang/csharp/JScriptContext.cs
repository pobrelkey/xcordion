using System;
using System.CodeDom;
using System.IO;
using System.CodeDom.Compiler;
using System.Reflection;
using Microsoft.JScript;

using xcordion.api;
using xcordion.util;


namespace xcordion.lang.csharp
{
    public class JScriptContext : EvaluationContext<JScriptContext>
    {
        // thanks, http://www.odetocode.com/Code/80.aspx
        private static object evaluator = null;
        private static Type evaluatorType = null;
        static JScriptContext()
        {
            JScriptCodeProvider jscript = new JScriptCodeProvider();

            CodeCompileUnit ast;
            using (Stream source = Assembly.GetCallingAssembly().GetManifestResourceStream(typeof(JScriptContext), "JScriptEvaluator.js")) 
            {
                using (StreamReader reader = new StreamReader(source)) 
                {
                    ast = jscript.Parse(reader);
                }
            }

            CompilerParameters parameters = new CompilerParameters();
            parameters.GenerateInMemory = true;

            CompilerResults results = jscript.CompileAssemblyFromDom(parameters, ast);
            Assembly assembly = results.CompiledAssembly;

            evaluatorType = assembly.GetType("xcordion.lang.jscript.Evaluator");
            evaluator = Activator.CreateInstance(evaluatorType);
        }

        private static object Eval(string statement)
        {
            return evaluatorType.InvokeMember(
                        "Eval",
                        BindingFlags.InvokeMethod,
                        null,
                        evaluator,
                        new object[] { statement }
                     );
        }



        internal JScriptContext(object rootObject)
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public JScriptContext subContext()
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public object eval<T>(string expression, T element) where T : class, TestElement<T>
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public object set<T>(string expression, T element) where T : class, TestElement<T>
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public Iterable<JScriptContext> iterate<T>(string expression, T element) where T : class, TestElement<T>
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public object getVariable(string name)
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public void setVariable(string name, object value)
        {
            throw new NotImplementedException("The method or operation is not implemented.");
        }

        public object getValue<T>(T element) where T : class, TestElement<T>
        {
            return getValue(element, null);
        }

        public object getValue<T>(T element, Type asClass) where T : class, TestElement<T>
        {
            return asClass != null ? System.Convert.ChangeType(element.getValue(), asClass) : element.getValue();
        }

    }
}
