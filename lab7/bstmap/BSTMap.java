package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable,V> implements Map61B<K,V>{


    private int size;
    public Entry entry;

    public BSTMap() {
        size = 0;
        entry = null;
    }

    @Override
    public void clear() {
        size = 0;
        entry = null;
    }

    
    private boolean findKey(Entry entry,K key) {
        if (entry == null) {
            return false;
        }
        if (entry.key() == key) {
            return true;
        }
        if (key.compareTo(entry.key()) < 0) {
            return findKey(entry.lson(),key);
        }
        return findKey(entry.rson(),key);
    }
    @Override
    public boolean containsKey(K key) {
        if (findKey(entry,key)) {
            return true;
        }
        return false;
    }

    private V findVal(Entry entry,K key) {
        if (entry == null) {
            return null;
        }
        if (entry.key().compareTo(key) == 0) {
            return entry.val();
        }
        if (key.compareTo(entry.key()) < 0) {
            return findVal(entry.lson(),key);
        }
        return findVal(entry.rson(),key);
    }

    @Override
    public V get(K key) {
        return findVal(entry,key);
    }

    @Override
    public int size() {
        return size;
    }

    private void putVal(Entry entry,K key, V val) {
        if (entry == null) {
            entry = new Entry(key,val);
            return;
        }
        if (entry.key().compareTo(key) == 0) {
            entry.put(val);
            return;
        }
        if (key.compareTo(entry.key()) < 0) {
            putVal(entry.lson(),key,val);
        } else {
            putVal(entry.rson(),key,val);
        }
    }

    @Override
    public void put(K key, V value) {
        putVal(entry,key,value);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    private class Entry {
        private K key;
        private V val;
        private Entry lson,rson;
        public Entry(K k,V v) {
            key = k;
            val = v;
            lson = rson = null;
        }

        public void put(V newval) {
            val = newval;
        }

        public Entry lson() {
            return lson;
        }

        public Entry rson() {
            return rson;
        }

        public K key() {
            return key;
        }

        public V val() {
            return val;
        }
    }
}
