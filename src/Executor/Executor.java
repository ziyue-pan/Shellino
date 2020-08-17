package Executor;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class Executor {
    private static final InputStream console_in = System.in;
    private static final PrintStream console_out = System.out;

    public static ArrayList<Command> jobs = new ArrayList<Command>();

    public static void ResetRedirect() {
        System.setIn(console_in);
        System.setOut(console_out);
    }
}
