package Interpreter;

import java.util.HashSet;

public class Data {
    public static final HashSet<String> supported_commands = new HashSet<String>() {{
        add("bg");
        add("cd");
        add("clr");
        add("dir");
        add("echo");
        add("exec");
        add("exit");
        add("environ");
        add("fg");
        add("help");
        add("jobs");
        add("pwd");
        add("quit");
        add("set");
        add("shift");
        add("test");
        add("time");
        add("umask");
        add("unset");
        add("myshell");
    }};
}
