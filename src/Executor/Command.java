package Executor;

import Runtime.*;

import java.util.Date;

public class Command {

    // TODO bg
    public static void bg() {
        System.out.println("bg");
    }

    // TODO cd
    public static void cd(String path) {
        System.out.println("cd");
    }

    public static void clr() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // TODO dir
    public static void dir(String path) {
        System.out.println("dir");
    }

    // TODO echo
    public static void echo() {
        System.out.println("echo");
    }

    // TODO exec
    public static void exec() {
        System.out.println("exec");
    }

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










}
