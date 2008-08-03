using System;
using System.Collections.Generic;
using System.Text;

namespace xcordion.util
{
    public interface Iterable<T> : IEnumerable<T>
    {
        Iterator<T> iterator();
    }
}
