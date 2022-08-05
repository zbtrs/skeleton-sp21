package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {
    @Test
    public void specialtest() {
        ArrayDeque<Integer> obj = new ArrayDeque<>();
        ArrayDequeCorrect<Integer> obj2 = new ArrayDequeCorrect<>();
        obj.addLast(839);
        obj.addLast(306);
        obj.removeLast();
        obj.removeFirst();
        obj.addFirst(541);
        obj.addFirst(991);
        obj.addFirst(450);
        obj.removeFirst();
        obj.addFirst(912);
        obj.removeFirst();
        obj.removeFirst();
        obj.removeFirst();
        obj.addLast(412);
    }


    @Test
    public void test1() {
        ArrayDeque<Integer> obj = new ArrayDeque<>();
        ArrayDequeCorrect<Integer> obj2 = new ArrayDequeCorrect<>();
        int N = 50000;
        for (int i = 1; i <= N; i++) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                int val = StdRandom.uniform(0,1000);
                System.out.printf("obj.addFirst(%d);\n",val);
                obj.addFirst(val);
                obj2.addFirst(val);
                assertEquals(obj.size(),obj2.size());
            }
            if (operationNumber == 1) {
                int val = StdRandom.uniform(0,1000);
                System.out.printf("obj.addLast(%d);\n",val);
                obj.addLast(val);
                obj2.addLast(val);
                assertEquals(obj.size(),obj2.size());
            }
            if (operationNumber == 2 && obj2.size() > 0) {
                System.out.printf("obj.removeFirst();\n");
                int temp = obj.removeFirst();
                int temp2 = obj2.removeFirst();
                assertEquals(temp,temp2);
            }
            if (operationNumber == 3 && obj2.size() > 0) {
                System.out.printf("obj.removeLast();\n");
                int temp = obj.removeLast();
                int temp2 = obj2.removeLast();
                assertEquals(temp,temp2);
            }
            if (operationNumber == 4) {
                int index = StdRandom.uniform(0,obj2.size() + 1);
                System.out.println("get " + index);
                assertEquals(obj.get(index),obj2.get(index));
            }
        }
    }
}
