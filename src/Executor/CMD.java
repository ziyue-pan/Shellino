package Executor;

import Runtime.*;

import java.io.PrintStream;
import java.util.Date;

public class CMD {

    // TODO bg
    // 用法：
    // ① bg %<pid>
    public static void bg() {
        System.out.println("bg");
    }

    // TODO cd
    // 用法：
    // ① cd
    // ② cd <path>
    public static void cd(String path) {
        System.out.println("cd");
    }

    // 用法：
    // ① clr
    public static void clr() {
        System.console().writer().print("\033[H\033[2J");
        System.console().flush();
    }

    // TODO dir
    // 用法：
    // ① dir
    // ② dir <path>
    public static void dir(String path) {
        System.out.println("dir");
    }

    // TODO echo
    // 用法：
    // echo <str 1> <str 2> <str 3> ...
    public static void echo() {
        System.out.println("echo");
    }

    // TODO exec
    // 用法:
    // exec <command>
    public static void exec() {
        System.out.println("exec");
    }

    // 用法:
    // exit
    public static void exit(int code) {
        System.exit(code);
    }

    // TODO environ
    public static void environ() {
        System.out.println("environ");
    }

    // TODO fg
    public static void fg() {
        System.out.println("fg");
    }

    // TODO help
    public static void help() {
        System.out.println("help");
    }

    // TODO jobs
    public static void jobs() {
        System.out.println("jobs");
    }

    public static void pwd() {
        System.out.println(Environment.Variables.get("PWD"));
    }

    public static void quit() {
        System.exit(0);
    }

    // TODO set
    public static void set() {
        System.out.println("set");
    }

    // TODO shift
    public static void shift() {
        System.out.println("shift");
    }

    // TODO test
    public static void test() {
        System.out.println("test");
    }

    public static void time() {
        Date date = new Date();
        System.out.println(date.toString());
    }

    // TODO umask
    public static void umask() {
        System.out.println("umask");
    }

    // TODO unset
    public static void unset() {
        System.out.println("unset");
    }

    // TODO redirect
    public static void Redirect() {

    }

    // TODO myshell
    public static void myshell() {

    }
}
