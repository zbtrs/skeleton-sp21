package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    private String SHA1,contents;

    public Blob(File obj) {
        contents = Utils.readContentsAsString(obj);
        SHA1 = Utils.sha1(contents);
    }

}
