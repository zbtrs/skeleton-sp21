package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    @Override
    public void clear() {
        size = 0;
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = null;
        }
    }


    private int gethashcode(K key,int mod) {
        int t = key.hashCode();
        t %= mod;
        if (t < 0) {
            t += mod;
        }
        return t;
    }
    @Override
    public boolean containsKey(K key) {
        int t = gethashcode(key,initialSize);
        for (Node temp : buckets[t]) {
            if (temp.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int t = gethashcode(key,initialSize);
        for (Node temp : buckets[t]) {
            if (temp.key.equals(key)) {
                return temp.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private void resize(int lastsize) {
        Collection<Node>[] newbuckets = new Collection[lastsize];
        for (int i = 0; i < lastsize; i++) {
            newbuckets[i] = createBucket();
        }
        for (int i = 0; i < initialSize; i++) {
            for (Node item : buckets[i]) {
                int t = gethashcode(item.key,lastsize);
                newbuckets[t].add(item);
            }
        }
        initialSize = lastsize;
        buckets = newbuckets;
    }
    @Override
    public void put(K key, V value) {
        int t = gethashcode(key,initialSize);
        for (Node temp : buckets[t]) {
            if (temp.key.equals(key)) {
                temp.value = value;
                return;
            }
        }
        size++;
        buckets[t].add(createNode(key,value));
        if ((double)(size / initialSize) > maxLoad) {
            resize(initialSize * 2);
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> result = new HashSet<>();
        for (int i = 0; i < initialSize; i++) {
            for (Node item : buckets[i]) {
                result.add(item.key);
            }
        }
        return result;
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            int t = gethashcode(key,initialSize);
            for (Node item : buckets[t]) {
                if (item.key.equals(key)) {
                    V result = item.value;
                    buckets[t].remove(item);
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (containsKey(key)) {
            int t = gethashcode(key,initialSize);
            for (Node item : buckets[t]) {
                if (item.key.equals(key)) {
                    if (item.value.equals(value)) {
                        V result = item.value;
                        buckets[t].remove(item);
                        return result;
                    }
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int initialSize = 16,size = 0;
    private double maxLoad = 0.75;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        buckets = new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        buckets = new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.maxLoad = maxLoad;
        buckets = new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

}
