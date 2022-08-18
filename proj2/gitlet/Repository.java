package gitlet;

import jh61b.junit.In;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public static final File ROOTCACHE_DIR = join(GITLET_DIR,"caches");
    public static final File ADDCACHE_DIR = join(ROOTCACHE_DIR,"add_caches");
    public static final File REMOVECACHE_DIR = join(ROOTCACHE_DIR,"remove_caches");

    public static Config config = new Config();
    private static final int INF = 0x7ffffff;


    /* TODO: fill in the rest of this class. */

    private void checkinit() {
        if (!GITLET_DIR.exists() || !GITLET_DIR.isDirectory()) {
            Utils.message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

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
        if (!ROOTCACHE_DIR.exists()) {
            ROOTCACHE_DIR.mkdir();
        }
        if (!ADDCACHE_DIR.exists()) {
            ADDCACHE_DIR.mkdir();
        }
        if (!REMOVECACHE_DIR.exists()) {
            REMOVECACHE_DIR.mkdir();
        }
        config.init();
        initialcommit();
    }

    private Commit getcurrentcommit() {
        Commit currentcommit = Utils.readObject(join(REFS_DIR, config.HEAD),Commit.class);
        return currentcommit;
    }
    public void remove(String filename) {
        checkinit();
        //如果这个文件在addcaches中，直接删除
        config.load();
        Commit currentcommit = getcurrentcommit();
        if (config.addcaches.contains(filename)) {
            File objfile = join(ADDCACHE_DIR,filename);
            objfile.delete();
            config.addcaches.remove(filename);
        } else if (currentcommit.contain(filename)) {
            //要先得到SHA1值，并在BLOB文件夹中找到这个,并写入到删除缓冲区中
            String filesha1 = currentcommit.getblobsha1(filename);
            Blob objblob = new Blob(join(BLOBS_DIR,filesha1));
            File objfile = join(REMOVECACHE_DIR,filename);
            Utils.writeContents(objfile,objblob.contents());
            config.removecaches.add(filename);

            //如果工作区存在这个文件，就直接删除
            File CWDfile = join(CWD,filename);
            if (CWDfile.exists()) {
                CWDfile.delete();
            }
        } else {
            Utils.message("No reason to remove the file.");
            System.exit(0);
        }
        config.store();
    }
    public void add(String filename) {
        checkinit();

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
        Commit currentcommit = getcurrentcommit();
        //先把remove_cache中的同名文件删掉
        File removefile = join(REMOVECACHE_DIR,filename);
        if (removefile.exists()) {
            removefile.delete();
        }

        if (currentcommit.containblob(objfile)) {
            //如果当前的commit中包含了这个文件，就要删除缓冲区中同名的文件
            config.remove_addcache(filename);
            join(ADDCACHE_DIR,filename).delete();
        } else {
            //在缓存区写入文件
            File objincache = join(ADDCACHE_DIR, filename);
            if (!objincache.exists()) {
                createfile(objincache);
            }
            Utils.writeContents(objincache, objfile.contents());
            //更新config
            config.add_addcache(filename);
        }
        config.store();
    }

    //commit信息都存储在.gitlet/refs中，HEAD,branchs,commits等信息存储在.gitlet中
    public void commit(String message,Date date) {
        //检查错误情况:缓存区中没有文件
        checkinit();
        config.load();
        if (config.addcaches.isEmpty() && config.removecaches.isEmpty()) {
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
        for (String item : config.addcaches) {
            File temp = join(ADDCACHE_DIR,item);
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
        config.addcaches.clear();
        //接下来处理删除
        for (String item : config.removecaches) {
            File temp = join(REMOVECACHE_DIR,item);
            Blob cacheblob = new Blob(temp);
            newcommit.removeblob(item,cacheblob);
            temp.delete();
        }
        config.removecaches.clear();

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
        checkinit();
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
        //TODO 有合并的情况
    }

    public void find(String message) {
        checkinit();
        boolean isfind = false;
        List<String> filenames = Utils.plainFilenamesIn(REFS_DIR);
        for (String filename : filenames) {
            Commit commit = Utils.readObject(join(REFS_DIR,filename),Commit.class);
            if (commit.message().equals(message)) {
                System.out.println(commit.SHA1());
                isfind = true;
            }
        }
        if (!isfind) {
            Utils.message("Found no commit with that message.");
            System.exit(0);
        }
    }
    public void globallog() {
        checkinit();
        List<String> filenames = Utils.plainFilenamesIn(REFS_DIR);
        for (String filename : filenames) {
            Commit commit = Utils.readObject(join(REFS_DIR,filename),Commit.class);
            commit.print();
        }
    }

    /**
     * 将commitid中的filename这个文件写到工作区中
     * opt为0表示当前commit,为1表示指定了commitid
     */
    public void checkoutfile(String commitid,String filename,int opt) {
        checkinit();
        config.load();
        if (opt == 0) {
            commitid = config.HEAD;
        }
        File commitfile = join(REFS_DIR,commitid);
        if (!commitfile.exists()) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
        }

        Commit commit = Utils.readObject(join(REFS_DIR,commitid),Commit.class);
        if (!commit.contain(filename)) {
            Utils.message("File does not exist in that commit.");
            System.exit(0);
        }
        Commit currentcommit = getcurrentcommit();
        File cwdfile = join(CWD,filename);
        if (!currentcommit.contain(filename) && config.addcaches.contains(filename) && cwdfile.exists()) {
            Utils.message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        //检查是否会覆盖unstaged的文件
        String filesha1 = commit.getblobsha1(filename);
        Blob objfile = new Blob(join(BLOBS_DIR,filesha1));
        File CWDfile = join(CWD,filename);
        if (!CWDfile.exists()) {
            createfile(CWDfile);
        }
        Utils.writeContents(CWDfile,objfile.contents());
        config.store();
    }

    public void checkoutbranch(String branchname,int opt) {
        checkinit();
        config.load();
        //先检查分支名是否存在
        if (!config.branch2commit.containsKey(branchname)) {
            Utils.message("No such branch exists.");
            System.exit(0);
        }
        if (config.branch.equals(branchname) && opt != 1) {
            Utils.message("No need to checkout the current branch.");
            System.exit(0);
        }
        //将当前的commit和要checkout的commit弄出来
        Commit currentcommit = getcurrentcommit();
        Commit objcommit = Utils.readObject(config.getbranchcommit(branchname),Commit.class);
        //遍历objcommit的所有blob,看是否存在一个在当前的工作区，并且不在currentcommit中
        boolean canoverwrite = true;
        for (String item : objcommit.blobnames()) {
            File cwdfile = join(CWD,item);
            if (cwdfile.exists() && !currentcommit.contain(item)) {
                canoverwrite = false;
                break;
            }
        }
        if (!canoverwrite) {
            Utils.message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        //更改HEAD和nowbranch
        config.HEAD = objcommit.SHA1();
        config.branch = branchname;
        //清空工作区的文件,危险！
        List<String> filenames = Utils.plainFilenamesIn(CWD);
        for (String filename : filenames) {
            File cwdfile = join(CWD,filename);
            if (cwdfile.exists()) {
                cwdfile.delete();
            }
        }
        //将objcommit的文件全部放到工作区中
        for (String item : objcommit.blobnames()) {
            Blob objblob = new Blob(join(BLOBS_DIR,objcommit.getblobsha1(item)));
            File cwdfile = join(CWD,item);
            createfile(cwdfile);
            Utils.writeContents(cwdfile,objblob.contents());
        }
        //清空两个cache区域,注意要更改config!
        for (String filename : config.addcaches) {
            File cachefile = join(ADDCACHE_DIR,filename);
            cachefile.delete();
        }
        config.addcaches.clear();
        for (String filename : config.removecaches) {
            File cachefile = join(REMOVECACHE_DIR,filename);
            cachefile.delete();
        }
        config.removecaches.clear();

        config.store();
    }

    public void branch(String branchname) {
        checkinit();
        config.load();
        if (config.branch2commit.containsKey(branchname)) {
            Utils.message("A branch with that name already exists.");
            System.exit(0);
        }
        File commit = join(REFS_DIR,config.HEAD);
        config.updatebranch(branchname,commit);
        config.store();
    }
    public void removebranch(String branchname) {
        checkinit();
        config.load();
        if (config.branch.equals(branchname)) {
            Utils.message("Cannot remove the current branch.");
            System.exit(0);
        }
        if (!config.branch2commit.containsKey(branchname)) {
            Utils.message("A branch with that name does not exist.");
            System.exit(0);
        }
        config.branch2commit.remove(branchname);
        config.store();
    }

    private void printstatus(ArrayList<String> stringstoprint,String message) {
        System.out.println(message);
        for (String item : stringstoprint) {
            System.out.println(item);
        }
        System.out.println();
    }

    public void status() {
        checkinit();
        config.load();
        ArrayList<String> stringstosort = new ArrayList<>();
        //branches
        for (String item : config.branch2commit.keySet()) {
            stringstosort.add(item);
        }
        stringstosort.sort(Comparator.naturalOrder());
        System.out.println("=== Branches ===");
        for (String item : stringstosort) {
            if (item.equals(config.branch)) {
                System.out.println("*"+config.branch);
            } else {
                System.out.println(item);
            }
        }
        System.out.println();
        //staged files
        stringstosort.clear();
        for (String item : config.addcaches) {
            stringstosort.add(item);
        }
        stringstosort.sort(Comparator.naturalOrder());
        printstatus(stringstosort,"=== Staged Files ===");
        //removed files
        stringstosort.clear();
        for (String item : config.removecaches) {
            stringstosort.add(item);
        }
        stringstosort.sort(Comparator.naturalOrder());
        printstatus(stringstosort,"=== Removed Files ===");
        //modification but not...
        Commit currentcommit = getcurrentcommit();
        stringstosort.clear();
        for (String item : currentcommit.blobnames()) {
            //如果在当前的工作目录中并且不在addcache中，但是文件内容发生了改变
            File cwdfile = join(CWD,item);
            //这里直接比较SHA1就可以
            File addcachefile = join(ADDCACHE_DIR,item);
            if (cwdfile.exists() && !addcachefile.exists()) {
                Blob cwdblob = new Blob(cwdfile);
                if (!cwdblob.SHA1().equals(currentcommit.getblobsha1(item))) {
                    stringstosort.add(item + " (modified)");
                }
            }
            //如果不在工作区中也不在removecaches中
            if (!cwdfile.exists() && !config.removecaches.contains(item)) {
                stringstosort.add(item + " (deleted)");
            }
        }
        //如果在addcaches中，但是不在工作区中或者addcaches中的文件内容和工作区的文件内容不同
        for (String item : config.addcaches) {
            File cwdfile = join(CWD,item);
            if (!cwdfile.exists()) {
                stringstosort.add(item + " (modified)");
            } else {
                File addcachefile = join(ADDCACHE_DIR,item);
                Blob cwdblob = new Blob(cwdfile);
                Blob addcacheblob = new Blob(addcachefile);
                if (!cwdblob.SHA1().equals(addcacheblob.SHA1())) {
                    stringstosort.add(item + " (modified)");
                }
            }
        }
        stringstosort.sort(Comparator.naturalOrder());
        printstatus(stringstosort,"=== Modifications Not Staged For Commit ===");
        //untracked file
        stringstosort.clear();
        for (String item : Utils.plainFilenamesIn(CWD)) {
            if (!currentcommit.contain(item) && !config.addcaches.contains(item)) {
                stringstosort.add(item);
            }
        }
        stringstosort.sort(Comparator.naturalOrder());
        printstatus(stringstosort,"=== Untracked Files ===");

        config.store();
    }

    public void reset(String commitid) {
        checkinit();
        config.load();
        if (!config.commit2file.containsKey(commitid)) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
        }
        File commitfile = join(REFS_DIR,commitid);
        config.updatebranch(config.branch,commitfile);
        config.HEAD = commitid;
        checkoutbranch(config.branch,1);

        config.store();
    }

    private Commit getlca(Commit A,Commit B) {
        HashMap<String,Integer> depthA = new HashMap<>();
        HashMap<String,Integer> depthB = new HashMap<>();
        HashSet<String> ancestorA = new HashSet<>();
        HashSet<String> ancestorB = new HashSet<>();
        BFS(A,depthA,ancestorA);
        BFS(B,depthB,ancestorB);
        int mindep = INF;
        String C;
        for (String item : ancestorA) {
            if (depthB.containsKey(item) && depthB.get(item) < mindep) {
                mindep = depthB.get(item);
                C = item;
            }
        }
        return Utils.readObject(join(REFS_DIR,C),Commit.class);
    }

    public void merge(String branchname) {
        checkinit();
        config.load();
        if (!config.addcaches.isEmpty()) {
            Utils.message("You have uncommitted changes.");
            System.exit(0);
        }
        if (!config.branch2commit.containsKey(branchname)) {
            Utils.message("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchname.equals(config.branch)) {
            Utils.message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit currentcommit = getcurrentcommit();
        Commit goalcommit = Utils.readObject(config.getbranchcommit(branchname), Commit.class);
        Commit LCAcommit = getlca(currentcommit,goalcommit);

        config.store();
    }
}
