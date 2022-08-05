package deque;

import net.sf.saxon.functions.PositionAndLast;

import java.util.Iterator;

public class LinkedListDeque<T> {
    public int size;
    public StuffNode obj,Presentinel,Lastsentinel;

    public LinkedListDeque() {
        Presentinel = new StuffNode();
        Lastsentinel = new StuffNode();
        Presentinel.last = Lastsentinel;
        Lastsentinel.pre = Presentinel;
        size = 0;
    }

    public void addFirst(T item) {
        Presentinel.pre = new StuffNode(0,Presentinel);
        Presentinel.val = item;
        Presentinel = Presentinel.pre;
        size += 1;
    }

    public void addLast(T item) {
        Lastsentinel.last = new StuffNode(1,Lastsentinel);
        Lastsentinel.val = item;
        Lastsentinel = Lastsentinel.last;
        size += 1;
    }

    public boolean isEmpty() {
        if (size > 0) {
            return false;
        }
        return true;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if (size == 0) {
            System.out.println();
            return;
        }
        StuffNode temp = Presentinel.last;
        System.out.printf("%s",temp.val.toString());
        temp = temp.last;
        while (temp != Lastsentinel) {
            System.out.printf(" %s",temp.val.toString());
            temp = temp.last;
        }
        System.out.println();
    }


    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        StuffNode temp = Presentinel.last;
        T tempval = temp.val;
        size -= 1;
        temp.last.pre = Presentinel;
        Presentinel.last = temp.last;
        temp = null;
        return tempval;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        StuffNode temp = Lastsentinel.pre;
        T tempval = temp.val;
        size -= 1;
        temp.pre.last = Lastsentinel;
        Lastsentinel.pre = temp.pre;
        temp = null;
        return tempval;
    }

    public T get(int index) {
        if (index > size) {
            return null;
        }
        StuffNode temp = Presentinel;
        for (int i = 1; i <= index; i++) {
            temp = temp.last;
        }
        return temp.val;
    }

    private T getRecursiveHelper(int index,StuffNode obj) {
        if (index == 0) {
            return obj.val;
        }
        return getRecursiveHelper(index - 1,obj.last);
    }

    public T getRecursive(int index) {
        if (index > size) {
            return null;
        }
        return getRecursiveHelper(index,Presentinel);
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque<?>)) {
            return false;
        }
        if (((LinkedListDeque<?>) o).size != size) {
            return false;
        }
        StuffNode temp1 = Presentinel.last;
        LinkedListDeque<?>.StuffNode temp2 = ((LinkedListDeque<?>) o).Presentinel.last;
        while (temp1 != Lastsentinel) {
            if (temp1.val != temp2.val) {
                return false;
            }
            temp1 = temp1.last;
            temp2 = temp2.last;
        }
        return true;
    }

    private class StuffNode {
        public StuffNode pre,last;
        public T val;
        public StuffNode() {
            pre = last = null;
        }

        public StuffNode(int opt,StuffNode obj) {
            if (opt == 0) {
                last = obj;
                pre = null;
            } else {
                pre = obj;
                last = null;
            }
        }
    }
}
