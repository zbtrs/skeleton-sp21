package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author zbtrs
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    


    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File REFS_DIR = join(GITLET_DIR,"refs");
    public static final File BLOBS_DIR = join(GITLET_DIR,"blobs");
    public static final File CACHE_DIR = join(GITLET_DIR,"caches");
    public static Config config = new Config();


    /* TODO: fill in the rest of this class. */

    private void initialcommit() {
        config.load();
        config.branch = "master";
        //创建第一个commit
        Commit newcommit = new Commit("initial commit",new Date(0));
        File newcommitfile = join(REFS_DIR,newcommit.SHA1());
        createfile(newcommitfile);
        Utils.writeObject(newcommitfile,newcommit);
        config.HEAD = newcommit.SHA1();
        config.updatecommit(newcommit,newcommitfile);
        config.updatebranch("master",newcommitfile);
        config.store();
    }

    public void init(){
        if (GITLET_DIR.exists() && GITLET_DIR.isDirectory()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        //当前工作目录中记录HEAD指针指向的commit的hashID,等到第一次commit后再写入内容
        //先把各个文件夹给创建好
        if (!REFS_DIR.exists()) {
            REFS_DIR.mkdir();
        }
        if (!BLOBS_DIR.exists()) {
            BLOBS_DIR.mkdir();
        }
        if (!CACHE_DIR.exists()) {
            CACHE_DIR.mkdir();
        }
        config.init();
        initialcommit();
    }

    public void add(String filename) {
        if (!GITLET_DIR.exists() || !GITLET_DIR.isDirectory()) {
            Utils.message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        File obj = join(CWD,filename);
        //如果要添加的文件不存在
        if (!obj.exists()) {
            Utils.message("File does not exist.");
            System.exit(0);
        }

        //读入要存入缓存区的文件
        Blob objfile = new Blob(obj);
        config.load();
        //根据HEAD找到最新的commit
        Commit currentcommit = Utils.readObject(join(REFS_DIR,config.HEAD),Commit.class);
        if (currentcommit.containblob(objfile)) {
            //如果当前的commit中包含了这个文件，就要删除缓冲区中同名的文件
            config.removecache(filename);
            join(CACHE_DIR,filename).delete();
        } else {
            //在缓存区写入文件
            File objincache = join(CACHE_DIR, filename);
            if (!objincache.exists()) {
                createfile(objincache);
            }
            Utils.writeContents(objincache, objfile.contents());
            //更新config
            config.addcache(filename);
        }
        config.store();
    }

    //commit信息都存储在.gitlet/refs中，HEAD,branchs,commits等信息存储在.gitlet中
    public void commit(String message,Date date) {
        //检查错误情况:缓存区中没有文件
        config.load();
        if (config.caches.isEmpty()) {
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }
        if (message.equals("")) {
            Utils.message("Please enter a commit message.");
            System.exit(0);
        }

        //首先读取head,即当前head指向的commit的sha1,根据这个sha1在对应文件夹中找到指定commit文件,创建一个新的commit
        String nowbranch = config.branch;
        File headfile = join(REFS_DIR,config.HEAD);
        Commit newcommit = new Commit(config.HEAD,headfile,message, date);
        for (String item : config.caches) {
            File temp = join(CACHE_DIR,item);
            Blob cacheblob = new Blob(temp);
            if (newcommit.contain(item)) {
                //如果commit中原来就包含了这个blobs,那么就需要更新
                newcommit.updateblob(item,cacheblob);
            } else {
                //如果没有包含，则需要添加
                newcommit.additem(item,cacheblob);
            }
            //将暂存区的blob写入到BLOBS_DIR中，用SHA1作为文件名，并且只写入blob中的文本内容
            File blob2dir = join(BLOBS_DIR,cacheblob.SHA1());
            Utils.createfile(blob2dir);
            Utils.writeContents(blob2dir,cacheblob.contents());

            temp.delete();
        }
        config.caches.clear();
        newcommit.update();
        File newcommitfile = join(REFS_DIR,newcommit.SHA1());
        Utils.createfile(newcommitfile);
        Utils.writeObject(newcommitfile,newcommit);
        config.updatecommit(newcommit,newcommitfile);
        config.updatebranch(nowbranch,newcommitfile);
        config.HEAD = newcommit.SHA1();
        config.store();
    }

    public void log() {
        config.load();
        Commit commit = Utils.readObject(join(REFS_DIR,config.HEAD),Commit.class);
        while (true) {
            commit.print();
            if (!commit.equalinitial()) {
                commit = Utils.readObject(join(REFS_DIR,commit.parent()),Commit.class);
            } else {
                break;
            }
        }
        //TODO 有合并的情况1
    }
}
