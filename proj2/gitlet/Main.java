package gitlet;

import java.util.Date;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    private static void test() {
        Test temp = new Test();
        temp.test();
    }

    public static void main(String[] args) {
        /*
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }
        */
        Repository object = new Repository();
        //object.add("test2.txt");

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                if (args.length != 1) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.init();
                break;
            case "add":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.add(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.commit(args[1],new Date());
                break;
            // TODO: FILL THE REST IN
        }






    }
}
