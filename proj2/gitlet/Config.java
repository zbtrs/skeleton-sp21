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
    public Map<String, File> commit2file = new HashMap<>();
    public Map<String,File> branch2commit = new HashMap<>();
    public Set<String> caches = new HashSet<>();

    private final File HEADfile = join(Repository.GITLET_DIR,"HEAD");
    private final File commits = join(Repository.GITLET_DIR,"commits");
    private final File branchs = join(Repository.GITLET_DIR,"branchs");
    private final File cachesfile = join(Repository.GITLET_DIR,"caches");
    private final File nowbranch = join(Repository.GITLET_DIR,"nowbranch");
    public String HEAD;

    public void store() {
        Utils.writeObject(commits, (Serializable) commit2file);
        Utils.writeObject(branchs,(Serializable) branch2commit);
        Utils.writeContents(HEADfile,HEAD);
        Utils.writeObject(cachesfile, (Serializable) caches);
    }
    public void init() {
        createfile(HEADfile);
        createfile(commits);
        createfile(branchs);
        createfile(cachesfile);
        createfile(nowbranch);
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
    public void load() {
        if (!commits.exists()) {
            createfile(commits);
        }
        if (!branchs.exists()) {
            createfile(branchs);
        }
        commit2file = Utils.readObject(commits,HashMap.class);
        branch2commit = Utils.readObject(branchs,HashMap.class);
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
