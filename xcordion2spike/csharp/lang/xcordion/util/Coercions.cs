using System;
using System.Collections;
using System.Text;
using System.Text.RegularExpressions;
using System.Collections.Generic;

namespace xcordion.util
{
    public static class Coercions
    {
        public static bool toBoolean(object p)
        {
            return Convert.ToBoolean(p);
        }

        internal static IEnumerable<object> toEnumerable(object p)
        {
            if (p is IEnumerable<object>)
            {
                return (IEnumerable<object>)p;
            }
            else if (p is IEnumerable)
            {
                List<object> result = new List<object>();
                foreach (object q in (IEnumerable) p) 
                {
                    result.Add(q);
                }
                return result;
            }
            else if (p != null)
            {
                return new Object[] { p };
            }
            else
            {
                return new Object[0];
            }
        }

        internal static string toStackTrace(Exception t)
        {
            return t.StackTrace;
        }

        internal static string toExceptionMessage(Exception e)
        {
            return e.Message;
        }

        private static readonly Regex DASH_CHARACTER = new Regex("-([a-z])");

        internal static string camelCase(string s)
        {
            if (s.IndexOf('-') == -1)
            {
                return s;
            }

            return DASH_CHARACTER.Replace(s, delegate(Match m)
            {
                return m.Groups[1].Value.ToUpper();
            });
        }
    }
}
