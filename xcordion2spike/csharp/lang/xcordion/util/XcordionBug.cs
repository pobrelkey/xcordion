using System;
using System.Collections.Generic;
using System.Text;

namespace xcordion.util
{
    public class XcordionBug : Exception
    {
        public XcordionBug()
            : base()
        {
        }

        public XcordionBug(string s)
            : base(s)
        {
        }

    }
}
