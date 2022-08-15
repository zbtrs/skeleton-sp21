package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.join;

public class Config {
    public Map<String, File> commit2file = new HashMap<>();
    public Map<String,File> branch2commit = new HashMap<>();

    public File HEADfile = join(Repository.GITLET_DIR,"HEAD");
    public String HEAD;

    public void createHEAD() {
        try {
            HEADfile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readHEAD() {
        if (!HEADfile.exists()) {
            createHEAD();
        }
        HEAD = Utils.readContentsAsString(HEADfile);
    }

    public void writeHEAD() {
        if (!HEADfile.exists()) {
            createHEAD();
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
