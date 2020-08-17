package Runtime;

import Interpreter.Data;

import java.io.*;
import java.util.Date;
import java.util.Scanner;

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
        System.out.print("\033[H\033[2J");
        System.out.flush();
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
    public static void echo(InputStream in, OutputStream out) {
        try {
            String word;
            BufferedWriter out_writer =
                    new BufferedWriter(new OutputStreamWriter(out));
            Scanner sc = new Scanner(in);
            out_writer.write("\r");
            while (sc.hasNext()) {
                word = sc.next();
                out_writer.write(word + " ");
            }
            out_writer.write("\n");
            out_writer.flush();
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
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
    // 用法:
    // fg %<pid>
    public static void fg() {
        System.out.println("fg");
    }

    // TODO help
    public static void help() {
        System.out.println("help");
    }


    public static void jobs(OutputStream out) {
        try {
            BufferedWriter out_writer =
                    new BufferedWriter(new OutputStreamWriter(out));

            out_writer.write("\rPID\t\tName\n");

            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (Data.supported_commands.contains(t.getName()))
                    out_writer.write((int) t.getId() + "\t\t" + t.getName() + "\n");
            }
            out_writer.flush();
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }

    public static void pwd() {
        System.out.println(Executor.variables.get("PWD"));
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

    public static void time(OutputStream out) {
        try {
            BufferedWriter out_writer =
                    new BufferedWriter(new OutputStreamWriter(out));
            Date date = new Date();
            out_writer.write("\r" + date.toString() + "\n");
            out_writer.flush();
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
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
