package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import static gitlet.Utils.join;

/**用来测试一些文件读入读写功能
 *
 */

public class Test implements Serializable{

    private String SHA1;
    private Testobj obj;

    private class Testobj implements Serializable {

    }

    /**
     * 测试读入文本文件
     */

    public void test() {
        File temp = join(Repository.CWD,"test.txt");
        String tempp = Utils.readContentsAsString(temp);
        File temp2 = join(Repository.CWD,"test2.txt");
        try {
            temp2.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeContents(temp2,tempp);
    }

    public static void test2() {
        Date temp = new Date();
        System.out.println(temp);
        Date temp2 = new Date(0);
        System.out.println(temp2);
    }
}
