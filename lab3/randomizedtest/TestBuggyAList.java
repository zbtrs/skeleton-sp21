package randomizedtest;

import afu.org.checkerframework.checker.igj.qual.I;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void test1() {
        AListNoResizing<Integer> temp1 = new AListNoResizing<>();
        BuggyAList<Integer> temp2 = new BuggyAList<>();
        for (int i = 1; i <= 3; i++) {
            temp1.addLast(i);
            temp2.addLast(i);
        }
        for (int i = 3; i >= 1; i--) {
            assertEquals(temp1.removeLast(),temp2.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L2 = new BuggyAList<>();

        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size2 = L2.size();
                assertEquals(size,size2);
                //System.out.println("size: " + size);
            } else if (operationNumber == 2 && L.size() > 0 && L2.size() > 0) {
                int lastval = L.getLast();
                int lastval2 = L2.getLast();
                assertEquals(lastval2,lastval);
                //System.out.println("lastval: " + lastval);
            } else if (operationNumber == 3 && L.size() > 0 && L2.size() > 0) {
                int lastval = L.removeLast();
                int lastval2 = L2.removeLast();
                assertEquals(lastval2,lastval);
                //System.out.println("remove: " + lastval);
            }
        }

    }
}
