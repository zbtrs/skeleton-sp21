package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;

public class Config {
    public HashMap<String, File> commit2file = new HashMap<>();
    public HashMap<String,File> branch2commit = new HashMap<>();
    public HashSet<String> caches = new HashSet<>();

    private final File HEADfile = join(Repository.GITLET_DIR,"HEAD");
    private final File commits = join(Repository.GITLET_DIR,"commits");
    private final File branchs = join(Repository.GITLET_DIR,"branchs");
    private final File cachesfile = join(Repository.GITLET_DIR,"cachesfile");
    private final File nowbranch = join(Repository.GITLET_DIR,"nowbranch");
    public String HEAD = "",branch = "";

    public void load() {
        commit2file = Utils.readObject(commits,HashMap.class);
        branch2commit = Utils.readObject(branchs,HashMap.class);
        caches = Utils.readObject(cachesfile,HashSet.class);
        HEAD = Utils.readContentsAsString(HEADfile);
        branch = Utils.readContentsAsString(nowbranch);
    }

    public void store() {
        Utils.writeObject(commits, (Serializable) commit2file);
        Utils.writeObject(branchs,(Serializable) branch2commit);
        Utils.writeObject(cachesfile, (Serializable) caches);
        Utils.writeContents(HEADfile,HEAD);
        Utils.writeContents(nowbranch,branch);
    }
    public void init() {
        createfile(HEADfile);
        createfile(commits);
        createfile(branchs);
        createfile(cachesfile);
        createfile(nowbranch);
        store();
    }

    public void removecache(String filename) {
        if (caches.contains(filename)) {
            caches.remove(filename);
        }
    }

    public void addcache(String filename) {
        if (caches.contains(filename)) {
            return;
        }
        caches.add(filename);
    }

    public void updatecommit(Commit newcommit,File newcommitfile) {
        commit2file.put(newcommit.SHA1(),newcommitfile);
    }

    public void updatebranch(String nowbranch,File newcommitfile) {
        branch2commit.put(nowbranch,newcommitfile);
    }

    public void loadcaches() {
        if (!cachesfile.exists()) {
            createfile(cachesfile);
        }
        caches = Utils.readObject(cachesfile,HashSet.class);
    }

    public void readHEAD() {
        if (!HEADfile.exists()) {
            createfile(HEADfile);
        }
        HEAD = Utils.readContentsAsString(HEADfile);
    }

    public String getnowbranch() {
        if (!nowbranch.exists()) {
            createfile(nowbranch);
        }
        return readContentsAsString(nowbranch);
    }

    public void setnowbranch(String result) {
        Utils.writeContents(nowbranch,result);
    }

    public File getcommit(String commit) {
        return commit2file.get(commit);
    }

    public File getbranch(String branch) {
        return branch2commit.get(branch);
    }

}
