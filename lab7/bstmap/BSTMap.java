package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V>{


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
        if (entry.key().compareTo(key) == 0) {
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

    private Entry putVal(Entry entry,K key, V val) {
        if (entry == null) {
            size++;
            return new Entry(key,val);
        }
        if (entry.key().compareTo(key) == 0) {
            entry.put(val);
            return entry;
        }
        if (key.compareTo(entry.key()) < 0) {
            entry.lson = putVal(entry.lson(),key,val);
            return entry;
        } else {
            entry.rson = putVal(entry.rson(),key,val);
            return entry;
        }
    }

    @Override
    public void put(K key, V value) {
        entry = putVal(entry,key,value);
    }

    private void getKeySet(Entry entry,HashSet<K> result) {
        if (entry == null) {
            return;
        }
        result.add(entry.key());
        getKeySet(entry.lson(),result);
        getKeySet(entry.rson(),result);
    }
    @Override
    public Set<K> keySet() {
        //throw new UnsupportedOperationException();
        HashSet<K> result = new HashSet<>();
        getKeySet(entry,result);
        return result;
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
