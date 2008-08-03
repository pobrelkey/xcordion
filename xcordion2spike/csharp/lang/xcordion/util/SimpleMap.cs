using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;

namespace xcordion.util
{
    public class SimpleMap<K,V> : IDictionary, IDictionary<K,V>, ICompatibleMap<K,V>
    {
        private Dictionary<K, V> peer = new Dictionary<K, V>();

        public bool containsKey(K key)
        {
            return ContainsKey(key);
        }

        public void put(K key, V value)
        {
            this[key] = value;
        }

        public V get(K key)
        {
            return ContainsKey(key) ? this[key] : default(V);
        }

        public IEnumerable<K> keySet()
        {
            return Keys;
        }

        #region IDictionary<K,V> Members

        public void Add(K key, V value)
        {
            this[key] = value;
        }

        public bool ContainsKey(K key)
        {
            return peer.ContainsKey(key);
        }

        public ICollection<K> Keys
        {
            get { return peer.Keys; }
        }

        public bool Remove(K key)
        {
            return peer.Remove(key);
        }

        public bool TryGetValue(K key, out V value)
        {
            return ((IDictionary<K,V>) peer).TryGetValue(key, out value);
        }

        public ICollection<V> Values
        {
            get { return peer.Values; }
        }

        public V this[K key]
        {
            get
            {
                return peer[key];
            }
            set
            {
                if (peer.ContainsKey(key))
                {
                    peer.Remove(key);
                }
                peer[key] = value;
            }
        }

        #endregion

        #region ICollection<KeyValuePair<K,V>> Members

        public void Add(KeyValuePair<K, V> item)
        {
            this[item.Key] = item.Value;
        }

        public void Clear()
        {
            peer.Clear();
        }

        public bool Contains(KeyValuePair<K, V> item)
        {
            return ((ICollection<KeyValuePair<K, V>>)peer).Contains(item);
        }

        public void CopyTo(KeyValuePair<K, V>[] array, int arrayIndex)
        {
            ((ICollection<KeyValuePair<K, V>>)peer).CopyTo(array, arrayIndex);
        }

        public int Count
        {
            get { return peer.Count; }
        }

        public bool IsReadOnly
        {
            get { return ((ICollection<KeyValuePair<K, V>>)peer).IsReadOnly; }
        }

        public bool Remove(KeyValuePair<K, V> item)
        {
            return ((ICollection<KeyValuePair<K, V>>)peer).Remove(item);
        }

        #endregion

        #region IEnumerable<KeyValuePair<K,V>> Members

        public IEnumerator<KeyValuePair<K, V>> GetEnumerator()
        {
            return ((IEnumerable<KeyValuePair<K, V>>)peer).GetEnumerator();
        }

        #endregion

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return ((IEnumerable)peer).GetEnumerator();
        }

        #endregion

        #region IDictionary Members

        public void Add(object key, object value)
        {
            this[key] = value;
        }

        public bool Contains(object key)
        {
            return ((IDictionary)peer).Contains(key);
        }

        IDictionaryEnumerator IDictionary.GetEnumerator()
        {
            return ((IDictionary)peer).GetEnumerator();
        }

        public bool IsFixedSize
        {
            get { return ((IDictionary)peer).IsFixedSize; }
        }

        ICollection IDictionary.Keys
        {
            get { return ((IDictionary)peer).Keys; }
        }

        public void Remove(object key)
        {
            ((IDictionary)peer).Remove(key);
        }

        ICollection IDictionary.Values
        {
            get { return ((IDictionary)peer).Values; }
        }

        public object this[object key]
        {
            get
            {
                return ((IDictionary)peer)[key];
            }
            set
            {
                if (key is K && peer.ContainsKey((K)key))
                {
                    peer.Remove((K)key);
                }
                ((IDictionary)peer)[key] = value;
            }
        }

        #endregion

        #region ICollection Members

        public void CopyTo(Array array, int index)
        {
            ((ICollection) peer).CopyTo(array, index);
        }

        public bool IsSynchronized
        {
            get { return ((ICollection) peer).IsSynchronized; }
        }

        public object SyncRoot
        {
            get { return ((ICollection) peer).SyncRoot; }
        }

        #endregion
    }
}
