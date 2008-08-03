using System;
using System.Collections.Generic;
using System.Text;

using xcordion.api;
using xcordion.util;


namespace xcordion.lang.csharp
{
    public class JScript : EvaluationContextFactory<JScriptContext>
    {
        public JScriptContext newContext(string languageName, object rootObject)
        {
            if (!string.IsNullOrEmpty(languageName))
            {
                string lang = languageName.ToLower();
                if (!lang.Equals("javascript") || lang.Equals("jscript"))
                {
                    throw new NotSupportedException("language \"" + languageName + "\" not supported");
                }
            }

            return new JScriptContext(rootObject);
        }
    }
}
