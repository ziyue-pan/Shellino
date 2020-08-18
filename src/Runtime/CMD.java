package Runtime;

import Interpreter.Data;
import Interpreter.Interpreter;
import Utilities.Common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Scanner;

public class CMD {

    // 用法：
    // ① bg %<pid>
    public static void bg(InputStream in) {
        try {
            Scanner sc = new Scanner(in);
            if (!sc.hasNext()) {
                System.out.println("[RuntimeError] no pid provided.");
                return;
            }
            long pid = sc.nextLong();
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (Data.supported_commands.contains(t.getName()) && t.getId() == pid) {
                    t.notify();
                    return;
                }
            }
            System.out.println("\rNo such foreground process");
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }

    // 用法：
    // ① cd
    // ② cd <path>
    public static void cd(InputStream in) {
        try {
            Scanner sc = new Scanner(in);
            if (sc.hasNext()) {
                String target = Common.GetAbsolutePath(sc.next());
                Path p = Paths.get(target);
                if (Files.exists(p) && Files.isDirectory(p))
                    Executor.variables.put("PWD", p.normalize().toString());
                else
                    System.out.println("[RuntimeError] No such directory " + p.toString());
            } else
                Executor.variables.put("PWD", Executor.variables.get("HOME"));

            System.out.print("\r");
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }

    // 用法：
    // ① clr
    public static void clr() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // 用法：
    // ① dir
    // ② dir <path>
    public static void dir(InputStream in, OutputStream out) {
        try {
            Scanner sc = new Scanner(in);
            File dir;
            if (sc.hasNext()) {
                String target = Common.GetAbsolutePath(sc.next());
                dir = new File(target);
                if (!(dir.exists() && dir.isDirectory())) {
                    System.out.println("[RuntimeError] No such directory: " + target);
                    return;
                }
            } else {
                dir = new File(Executor.variables.get("PWD"));
            }

            BufferedWriter out_writer =
                    new BufferedWriter(new OutputStreamWriter(out));
            File[] files = dir.listFiles();
            for (File f : files) {
                out_writer.write(f.getName() + " ");
            }

            out_writer.flush();
            System.out.print("\n\r");
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }

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

    // 用法:
    // exec <command>
    public static void exec(InputStream in) {
        try {
            Scanner sc = new Scanner(in);
            if (sc.hasNextLine()) {
                String c = sc.nextLine();
                if (c.contains("|")) {
                    System.out.println("[RuntimeError] exec does not support pipes.");
                    return;
                }
                Interpreter.ProcessCMD(c);
                System.exit(0);
            } else
                System.out.println("[RuntimeError] missing commands.");
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }

    }

    // 用法:
    // exit
    public static void exit(InputStream in) {
        try {
            Scanner sc = new Scanner(in);
            if (sc.hasNextInt())
                System.exit(sc.nextInt());
            else
                System.exit(0);
        } catch (Exception e) {
            System.exit(1);
        }
    }

    // 用法:
    // environ
    public static void environ(OutputStream out) {
        try {
            BufferedWriter out_writer =
                    new BufferedWriter(new OutputStreamWriter(out));
            for (Entry<String, String> entry : Executor.variables.entrySet())
                out_writer.write(entry.getKey() + "\t\t" + entry.getValue() + "\n");
            out_writer.flush();
            System.out.print("\r");
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }

    // 用法:
    // fg %<pid>
    public static void fg(InputStream in) {
        try {
            Scanner sc = new Scanner(in);
            if (!sc.hasNext()) {
                System.out.println("[RuntimeError] no pid provided.");
                return;
            }
            long pid = sc.nextLong();
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (Data.supported_commands.contains(t.getName()) && t.getId() == pid) {
                    t.join();
                    return;
                }
            }
            System.out.println("[RuntimeError] No such foreground process");
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }

    // TODO help
    public static void help() {
        System.out.println("help");
    }

    // 用法:
    // jobs
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

    // 用法:
    // pwd
    public static void pwd(OutputStream out) {
        try {
            BufferedWriter out_writer =
                    new BufferedWriter(new OutputStreamWriter(out));
            out_writer.write(Executor.variables.get("PWD"));
            out_writer.flush();
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
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

    // 用法:
    // time
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

    // 用法:
    // ① umask
    // ② umask <val>
    public static void umask(InputStream in, OutputStream out) {
        try {
            Scanner sc = new Scanner(in);

            if (sc.hasNextInt(8)) {
                int new_umask = sc.nextInt(8);
                if (new_umask >= 0 && new_umask <= 0777) {
                    Executor.variables.put("UMASK", Integer.toOctalString(new_umask));
                } else {
                    System.out.println("[RuntimeError] Umask out of range: "
                            + Integer.toOctalString(new_umask));
                }
            } else {
                BufferedWriter out_writer =
                        new BufferedWriter(new OutputStreamWriter(out));
                out_writer.write(Integer.parseInt(
                        Executor.variables.get("UMASK"), 8));
                out_writer.flush();
            }
        } catch (Exception e) {

        }
        System.out.println("umask");
    }

    // TODO unset
    public static void unset() {
        System.out.println("unset");
    }

    // TODO myshell
    public static void myshell() {

    }
}
