package deque;

public class ArrayDequeCorrect<T> {
    private Object[] array;
    private int size,pre,last;

    public ArrayDequeCorrect() {
        array = new Object[200000];
        size = 0;
        last = 100001;
        pre = last - 1;
    }

    public void addFirst(T item) {
        size++;
        array[pre] = item;
        pre--;
    }

    public void addLast(T item) {
        size++;
        array[last] = item;
        last++;
    }

    public int size() {
        return size;
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size--;
        pre++;
        T temp = (T) array[pre];
        array[pre] = null;
        return temp;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size--;
        last--;
        T temp = (T) array[last];
        array[last] = null;
        return temp;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int cur = pre + index + 1;
        return (T)array[cur];
    }
}
