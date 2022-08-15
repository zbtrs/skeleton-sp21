package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.join;

public class Config {
    public Map<String, File> commit2file = new HashMap<>();
    public Map<String,File> branch2commit = new HashMap<>();
    public Set<File> caches = new HashSet<>();

    public File HEADfile = join(Repository.GITLET_DIR,"HEAD");
    public File commits = join(Repository.GITLET_DIR,"commits");
    public File branchs = join(Repository.GITLET_DIR,"branchs");
    public File cachesfile = join(Repository.GITLET_DIR,"caches");
    public String HEAD;

    public void createfile(File obj) {
        try {
            obj.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void storecommits() {
        if (!commits.exists()) {
            createfile(commits);
        }
        Utils.writeObject(commits, (Serializable) commit2file);
    }

    public void storebranchs() {
        if (!branchs.exists()) {
            createfile(branchs);
        }
        Utils.writeObject(branchs,(Serializable) branch2commit);
    }
    public void readHEAD() {
        if (!HEADfile.exists()) {
            createfile(HEADfile);
        }
        HEAD = Utils.readContentsAsString(HEADfile);
    }

    public void writeHEAD() {
        if (!HEADfile.exists()) {
            createfile(HEADfile);
        }
        Utils.writeObject(HEADfile,HEAD);
    }

    public File getcommit(String commit) {
        return commit2file.get(commit);
    }

    public File getbranch(String branch) {
        return branch2commit.get(branch);
    }

}
