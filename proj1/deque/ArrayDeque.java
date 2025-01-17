package deque;

public class ArrayDeque<T> implements Deque<T> {
    private Object[] array;
    private int size,pre,last;
    public ArrayDeque() {
        array = new Object[8];
        size = pre = 0;
        last = 1;
    }

    private int movepre(int x) {
        x--;
        if (x < 0) {
            x = array.length - 1;
        }
        return x;
    }

    private int movelast(int x) {
        x++;
        if (x >= array.length) {
            x = 0;
        }
        return x;
    }


    private void resizebig(int sz) {
        Object[] newarray = new Object[sz];
        System.arraycopy(array, pre + 1, newarray, 0, array.length - pre - 1);
        System.arraycopy(array, 0, newarray, array.length - pre - 1, pre + 1);
        array = newarray;
        pre = sz - 1;
        last = size;
    }

    private void resizesmall(int sz) {
        Object[] newarray = new Object[sz];
        if (pre < last) {
            System.arraycopy(array,pre + 1,newarray,0,last - pre - 1);
        } else {
            System.arraycopy(array, pre + 1, newarray, 0, array.length - pre - 1);
            System.arraycopy(array, 0, newarray, array.length - pre - 1, last);
        }
        array = newarray;
        pre = sz - 1;
        last = size;
    }
    /*
    private void resize(int sz) {
        Object[] newarray = new Object[sz];
        if (pre < last && size < array.length) {
            System.arraycopy(array,pre + 1,newarray,0,last - pre - 1);
        } else if (size == array.length) {
            System.arraycopy(array, pre + 1, newarray, 0, array.length - pre - 1);
            System.arraycopy(array, 0, newarray, array.length - pre - 1, pre + 1);
        } else {
            System.arraycopy(array, pre + 1, newarray, 0, array.length - pre - 1);
            System.arraycopy(array, 0, newarray, array.length - pre - 1, last);
        }
        array = newarray;
        pre = sz - 1;
        last = size;
    }*/

    public void addFirst(T item) {
        if (size >= array.length) {
            resizebig((int)(size * 2));
        }
        size++;
        array[pre] = item;
        pre = movepre(pre);
    }

    public void addLast(T item) {
        if (size >= array.length) {
            resizebig((int)(size * 2));
        }
        size++;
        array[last] = item;
        last = movelast(last);
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int cur = pre;
        for (int i = 1; i <= size; i++) {
            cur = movelast(cur);
            if (i == 1) {
                System.out.printf("%s",array[cur].toString());
            } else {
                System.out.printf(" %s",array[cur].toString());
            }
        }
        System.out.println();
    }

    private void checksize() {

        if (size < array.length / 4 && array.length / 4 >= 1) {
            resizesmall(array.length / 4);
        }


    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size--;
        pre = movelast(pre);
        T temp = (T) array[pre];
        array[pre] = null;
        checksize();
        return temp;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size--;
        last = movepre(last);
        T temp = (T) array[last];
        array[last] = null;
        checksize();
        return temp;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int cur = pre + index + 1;
        if (cur >= array.length) {
            cur -= array.length;
        }
        return (T)array[cur];
    }

    public boolean equals(Object o) {
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        if (((ArrayDeque<?>) o).size != size) {
            return false;
        }
        int cur1 = pre,cur2 = ((ArrayDeque<?>) o).pre;
        for (int i = 1; i <= size; i++) {
            cur1 = movelast(cur1);
            cur2 = movelast(cur2);
            if (array[cur1] != ((ArrayDeque<?>) o).array[cur2]) {
                return false;
            }
        }
        return true;
    }

}
