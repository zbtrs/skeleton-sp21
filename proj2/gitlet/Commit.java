package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable{
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
    private Set<String> blobnames;
    private Map<String,String> blobsha1;
    private class commitcontents implements Serializable {
        private String message,parentcommit;
        private Date createDate;
        private Set<Blob> blobs;

        public void update(Blob oldblob,Blob newblob) {
            blobs.remove(oldblob);
            blobs.add(newblob);
        }
    }

    public Commit(String message,Date date) {
        blobnames = new HashSet<>();
        blobsha1 = new HashMap<>();
        obj.message = message;
        obj.parentcommit = "";
        obj.createDate = date;
        obj.blobs = new HashSet<>();
        SHA1 = Utils.sha1(obj);
    }

    public Commit(String parentid,File parentcommit,String message,Date date) {
        Commit temp = Utils.readObject(parentcommit,Commit.class);
        this.obj = temp.obj;;
        this.SHA1 = temp.SHA1;
        this.blobnames = temp.blobnames;
        this.blobsha1 = temp.blobsha1;
        obj.message = message;
        obj.createDate = date;
        obj.parentcommit = parentid;
    }
    public String getSHA1() {
        SHA1 = Utils.sha1(obj);
        return SHA1;
    }

    public boolean contain(String item) {
        return blobnames.contains(item);
    }

    public void additem(String item,Blob newblob) {
        blobnames.add(item);
        obj.blobs.add(newblob);
    }

    public void updateblob(String name,Blob newblob) {
        String oldblobsha1 = blobsha1.get(name);
        File oldblobfile = join(Repository.BLOBS_DIR,oldblobsha1);
        Blob oldblob = Utils.readObject(oldblobfile,Blob.class);
        obj.update(oldblob,newblob);
        blobsha1.put(name,newblob.SHA1());
    }

    public void update() {
        SHA1 = Utils.sha1(obj);
    }

    public String SHA1() {
        return SHA1;
    }

    /* TODO: fill in the rest of this class. */
}
