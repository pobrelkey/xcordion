using System;
using System.Collections.Generic;
using System.Text;

namespace xcordion.util
{
    public class WrappingIterable<I, O> : Iterable<O>
    {
        public delegate O Wrapper(I baseValue);
        
        private IEnumerable<I> peer;
        private Wrapper wrapper;

        public WrappingIterable(IEnumerable<I> peer, Wrapper wrapper)
        {
            this.peer = peer;
            this.wrapper = wrapper;
        }

        #region Iterable<O> Members

        public Iterator<O> iterator()
        {
            return new WrappingIterator<I, O>(peer.GetEnumerator(), wrapper);
        }

        #endregion

        #region IEnumerable<O> Members

        public IEnumerator<O> GetEnumerator()
        {
            return new WrappingEnumerator<I, O>(peer.GetEnumerator(), wrapper);
        }

        #endregion

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return new WrappingEnumerator<I, O>(peer.GetEnumerator(), wrapper);
        }

        #endregion
    }

    internal class WrappingIterator<I, O> : Iterator<O>
    {
        private IEnumerator<I> peer;
        private WrappingIterable<I, O>.Wrapper wrapper;
        private bool? more = default(bool?);

        internal WrappingIterator(IEnumerator<I> peer, WrappingIterable<I, O>.Wrapper wrapper)
        {
            this.peer = peer;
            this.wrapper = wrapper;
        }

        public bool hasNext()
        {
            if (!more.HasValue) 
            {
                more = peer.MoveNext();
            }
            return more.Value;
        }

        public O next()
        {
            hasNext();
            more = default(bool?);
            return wrapper(peer.Current);
        }
    }

    internal class WrappingEnumerator<I, O> : IEnumerator<O>, System.Collections.IEnumerator
    {
        private IEnumerator<I> peer;
        private WrappingIterable<I, O>.Wrapper wrapper;

        internal WrappingEnumerator(IEnumerator<I> peer, WrappingIterable<I, O>.Wrapper wrapper)
        {
            this.peer = peer;
            this.wrapper = wrapper;
        }


        #region IEnumerator<O> Members

        public O Current
        {
            get { return wrapper(peer.Current); }
        }

        #endregion

        #region IDisposable Members

        public void Dispose()
        {
            peer.Dispose();
        }

        #endregion

        #region IEnumerator Members

        object System.Collections.IEnumerator.Current
        {
            get { return wrapper(peer.Current); }
        }

        public bool MoveNext()
        {
            return peer.MoveNext();
        }

        public void Reset()
        {
            peer.Reset();
        }

        #endregion
    }
}
