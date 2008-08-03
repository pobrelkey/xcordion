using System;
using System.Collections.Generic;
using System.Text;

namespace xcordion.util
{
    public interface ICompatibleList<T> : IList<T>, Iterable<T>
    {
    }
}
