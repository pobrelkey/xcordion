using System;
using System.Collections.Generic;
using System.Text;
using xcordion.api;

namespace xcordion.lang.csharp
{
    public class OgnlContextFactory : EvaluationContextFactory<OgnlEvaluationContext>
    {

        public OgnlEvaluationContext newContext(string languageName, object rootObject)
        {
            if (!languageName.ToLower().Equals("ognl"))
            {
                return null;
            }
            return new OgnlEvaluationContext(rootObject);
        }

    }
}
