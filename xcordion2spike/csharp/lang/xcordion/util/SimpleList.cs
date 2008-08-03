using System;
using System.Collections.Generic;
using System.Text;

namespace xcordion.util
{
    public class SimpleList<T> : List<T>, ICompatibleList<T>
    {
        public int size()
        {
            return Count;
        }

        public void add(T item)
        {
            Add(item);
        }

        public T get(int index)
        {
            return this[index];
        }

        public void add(int index, T item)
        {
            Insert(index, item);
        }

        public void set(int index, T item)
        {
            this[index] = item;
        }

        public Iterator<T> iterator()
        {
            return new SimpleIterator<T>(GetEnumerator());
        }

        public void clear()
        {
            this.Clear();
        }
    }
}
