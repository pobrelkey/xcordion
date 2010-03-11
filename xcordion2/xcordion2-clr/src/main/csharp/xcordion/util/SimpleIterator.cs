using System;
using System.Collections.Generic;
using System.Text;

namespace xcordion.util
{
    public class SimpleIterator<T> : Iterator<T>
    {
        private IEnumerator<T> enumerator;
        private bool? more = default(bool?);

        public SimpleIterator(IEnumerator<T> e) 
        {
            this.enumerator = e;
        }

        public bool hasNext()
        {
            if (!more.HasValue) 
            {
                more = enumerator.MoveNext();
            }
            return more.Value;
        }

        public T next()
        {
            hasNext();
            more = default(bool?);
            return enumerator.Current;
        }

    }
}
