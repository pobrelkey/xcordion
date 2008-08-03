using System;
using System.Collections.Generic;
using System.Text;

namespace xcordion.util
{
    public interface Iterator<T>
    {
        bool hasNext();
        T next();
    }
}
