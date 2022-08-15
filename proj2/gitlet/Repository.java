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
    public Config config;


    /* TODO: fill in the rest of this class. */

    private void initialcommit() {
        Commit newcommit = new Commit("initial commit",new Date(0));
        File newcommitfile = join(REFS_DIR,newcommit.SHA1());
        createfile(newcommitfile);
        config.HEAD = newcommit.SHA1();
        config.updatecommit(newcommit,newcommitfile);
        String nowbranch = "master";
        config.updatebranch(nowbranch,newcommitfile);
        config.setnowbranch(nowbranch);

        config.store();
    }

    public void init(){
        if (GITLET_DIR.exists() && GITLET_DIR.isDirectory()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        //当前工作目录中记录HEAD指针指向的commit的hashID,等到第一次commit后再写入内容
        File HEAD = join(CWD,"HEAD");
        //创建HEAD文件
        try {
            HEAD.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        //如果是第一次调用add操作，创建对应的文件夹
        File adddir = join(GITLET_DIR,"cache");
        if (!adddir.exists()) {
            adddir.mkdir();
        }

        File obj = join(CWD,filename);
        //如果要添加的文件不存在
        if (!obj.exists()) {
            Utils.message("File does not exist.");
            System.exit(0);
        }

        //读入要存入缓存区的文件
        Blob objfile = new Blob(obj);
        //TODO 和当前commit的同名的文件进行比较，如果完全相等则将缓存区中对应的文件给删除并且这次操作不要add对应的文件

        //在缓存区写入文件
        File obj2 = join(GITLET_DIR,"cache",objfile.SHA1());
        if (!obj2.exists()) {
            try {
                obj2.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils.writeObject(obj2,objfile);
    }

    //commit信息都存储在.gitlet/refs中，HEAD,branchs,commits等信息存储在.gitlet中
    public void commit(String message,Date date) {
        //检查错误情况:缓存区中没有文件
        config.loadcaches();
        if (config.caches.isEmpty()) {
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }
        if (message.equals("")) {
            Utils.message("Please enter a commit message.");
            System.exit(0);
        }

        //TODO 读取当前分支/HEAD
        //首先读取head,即当前head指向的commit的sha1,根据这个sha1在对应文件夹中找到指定commit文件,创建一个新的commit
        config.readHEAD();
        String nowbranch = config.getnowbranch();
        File headfile = join(REFS_DIR,config.HEAD);
        Commit newcommit = new Commit(config.HEAD,headfile,message, date);
        for (String item : config.caches) {
            File temp = join(CACHE_DIR,item);
            Blob cacheblob = new Blob(temp);
            if (newcommit.contain(item)) {
                //如果commit中原来就包含了这个blobs,那么就需要更新
                //如何计算得到这个blob的hash值呢？
                //在新的commit的set中把原来的blob给删掉替换上新的blob
                newcommit.updateblob(item,cacheblob);
            } else {
                //如果没有包含，则需要添加
                newcommit.additem(item,cacheblob);
            }
            temp = join(BLOBS_DIR,cacheblob.SHA1());
            Utils.createfile(temp);
            Utils.writeContents(temp,cacheblob.contents());
        }
        config.caches.clear();

        newcommit.update();
        File newcommitfile = join(REFS_DIR,newcommit.SHA1());
        Utils.createfile(newcommitfile);
        config.updatecommit(newcommit,newcommitfile);
        config.updatebranch(nowbranch,newcommitfile);

        config.store();
    }

}
