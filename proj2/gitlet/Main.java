package gitlet;

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
        test();
        /*
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }
        Repository object = new Repository();
        object.load();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                if (args.length != 1) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if (args.length != 2) {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                object.add(args[1]);
                break;
            // TODO: FILL THE REST IN
        }

         */
    }
}
