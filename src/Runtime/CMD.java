package Runtime;

import Interpreter.Data;
import Interpreter.Interpreter;
import Utilities.Common;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
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
    // fg <%pid>
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

    // 用法:
    // help
    public static void help(OutputStream out) {
        try {
            BufferedWriter out_writer =
                    new BufferedWriter(new OutputStreamWriter(out));
            Files.copy(Paths.get(System.getProperty("user.dir") + "/README"), out);
            out_writer.write("\n");
            out_writer.flush();
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }

    // 用法:
    // more help
    public static void more_help() {
        try {
            List<String> lines = Files.readAllLines(
                    Paths.get(System.getProperty("user.dir") + "/README"));
            int page_num = 0, max_lines = lines.size(), max_pages = max_lines / Common.line_num;
            String choice;
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.printf(">>> Page %d (Total: %d)\n", page_num + 1, max_pages);
                for (int i = page_num * Common.line_num;
                     i < Math.min(max_lines, (page_num + 1) * Common.line_num); i++) {
                    System.out.println(lines.get(i));
                }
                System.out.print(":");
                choice = sc.nextLine();
                switch (choice) {
                    case "q":
                        return;
                    case "u":
                        if (page_num > 0)
                            page_num--;
                        break;
                    case "d":
                        if (page_num < max_pages)
                            page_num++;
                        break;
                    default:
                        System.out.println("Wrong choice!");
                        sc.nextLine();
                        break;
                }
            }


        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
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

    // 用法:
    // quit
    public static void quit() {
        System.exit(0);
    }

    // 用法:
    // set <key> <value>
    public static void set(InputStream in) {
        try {
            Scanner sc = new Scanner(in);
            String key, value;
            try {
                key = sc.next();
                value = sc.next();
            } catch (Exception e) {
                System.out.println("[RuntimeError] Cannot parse key & value.");
                return;
            }
            if (Data.secured_variables.contains(key)) {
                System.out.println("[RuntimeError] Cannot set secured variables.");
            } else {
                Executor.variables.put(key, value);
            }
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }

    // 用法:
    // shift <val> <arg1> <arg2> <arg3>
    public static void shift(InputStream in, OutputStream out, int shamt, boolean args_in) {
        try {
            Scanner sc = new Scanner(in);
            BufferedWriter out_writer =
                    new BufferedWriter(new OutputStreamWriter(out));
            shamt += args_in ? 1 : 0;
            String word;
            int cast = 0;
            while (sc.hasNext()) {
                word = sc.next();
                if (cast < shamt)
                    cast++;
                else
                    out_writer.write(word + " ");
            }
            out_writer.write("\n");
            out_writer.flush();
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }

    // 用法:
    // test -n <str>
    // test -z <str>
    public static void test(InputStream in, OutputStream out,
                            boolean exist, boolean args_in) {
        try {
            Scanner sc = new Scanner(in);
            BufferedWriter out_writer =
                    new BufferedWriter(new OutputStreamWriter(out));
            if (args_in)
                sc.next();
            if (exist && sc.hasNext() || (!exist && !sc.hasNext()))
                out_writer.write("true\n");
            else
                out_writer.write("false\n");
            out_writer.flush();
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
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
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }


    // 用法:
    // unset <key>
    public static void unset(InputStream in) {
        try {
            Scanner sc = new Scanner(in);
            if (sc.hasNext()) {
                String key = sc.next();
                if (Data.secured_variables.contains(key)) {
                    System.out.println("[RuntimeError] Cannot unset secured variables.");
                } else if (Executor.variables.containsKey(key)) {
                    Executor.variables.remove(key);
                } else {
                    System.out.println("[RuntimeError] No such key.");
                }
            } else {
                System.out.println("[RuntimeError] No key provided.");
            }
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }

    }

    // TODO myshell
    public static void myshell(InputStream in, OutputStream out) {
        try {
            Scanner sc = new Scanner(in);
            String filename;
            if (sc.hasNext())
                filename = sc.next();
            else {
                System.out.print("Enter a file path: ");
                sc = new Scanner(System.in);
                filename = sc.nextLine();
            }
            filename = Common.GetAbsolutePath(filename);

            try {
                File batchfile = new File(filename);
                BufferedReader br = new BufferedReader(new FileReader(batchfile));
                String line;
                while ((line = br.readLine()) != null)
                    Interpreter.ProcessCMD(line);
            } catch (Exception fe) {
                System.out.println("[RuntimeError] Cannot read file: " + filename);
            }
        } catch (Exception e) {
            System.out.println("[RuntimeError] " + e.getMessage());
        }
    }
}
