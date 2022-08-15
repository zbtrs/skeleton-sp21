package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String SHA1;
    private commitcontents obj;
    private class commitcontents implements Serializable {
        private String message,parentcommit;
        private Date createDate;
        private Set<String> blobs;


    }

    public Commit(String parentid,File parentcommit,String message,Date date) {
        obj = Utils.readObject(parentcommit,commitcontents.class);
        obj.message = message;
        obj.createDate = date;
        obj.parentcommit = parentid;
    }
    public String getSHA1() {
        SHA1 = Utils.sha1(obj);
        return SHA1;
    }

    /* TODO: fill in the rest of this class. */
}
