package Runtime;

import Interpreter.Interpreter;
import Utilities.Common;

import java.io.*;
import java.util.ArrayList;

public class Command implements Runnable {
    public String name;

    public ArrayList<String> args;
    public boolean background;
    public boolean running;

    public IOType input_type, output_type;
    public PipedInputStream pipe_in;
    public PipedOutputStream pipe_out;
    public String infile, outfile;

    public Command() {
        name = "";
        args = new ArrayList<>();
        background = running = false;
        input_type = IOType.ARGS_IN;
        output_type = IOType.CONSOLE_OUT;
        pipe_in = new PipedInputStream();
        pipe_out = new PipedOutputStream();
        infile = outfile = "";
    }

    @Override
    public void run() {
        running = true;
        InputStream in = null;
        OutputStream out = null;
        switch (input_type) {
            case REDIRECT_IN:
                try {
                    in = new FileInputStream(Common.GetAbsolutePath(infile));
                } catch (Exception e) {
                    System.out.println(
                            "[RuntimeError] Failed to create input stream from " + infile);
                    return;
                }
                break;
            case PIPE_IN:
                in = pipe_in;
                break;
            case ARGS_IN:
                StringBuilder sb = new StringBuilder();
                for (String s : args) {
                    sb.append(s).append(" ");
                }
                try {
                    in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
                } catch (Exception e) {
                    System.out.println("[RuntimeError] Failed to read in arguments.");
                    return;
                }
                break;
            default:
                System.out.println("[RuntimeError] Invalid input stream.");
                return;
        }

        switch (output_type) {
            case REDIRECT_OUT:
                try {
                    out = new FileOutputStream(Common.GetAbsolutePath(outfile));
                } catch (Exception e) {
                    try {
                        File file = new File(Common.GetAbsolutePath(outfile));
                        file.createNewFile();
                        out = new FileOutputStream(Common.GetAbsolutePath(outfile));
                    } catch (Exception ee) {
                        System.out.println("[RuntimeError] Failed to create output stream to" + outfile);
                        return;
                    }
                }
                break;
            case REDIRECT_APPEND:
                try {
                    out = new FileOutputStream(Common.GetAbsolutePath(outfile), true);
                } catch (Exception e) {
                    try {
                        File file = new File(Common.GetAbsolutePath(outfile));
                        file.createNewFile();
                        out = new FileOutputStream(Common.GetAbsolutePath(outfile), true);
                    } catch (Exception ee) {
                        System.out.println("[RuntimeError] Failed to create output stream to" + outfile);
                        return;
                    }
                }
                break;
            case PIPE_OUT:
                out = pipe_out;
                break;
            case CONSOLE_OUT:
                out = System.out;
                break;
        }

        switch (name) {
            case "bg":
                CMD.bg(in);
                break;
            case "cd":
                CMD.cd(in);
                break;
            case "clr":
                CMD.clr();
                break;
            case "dir":
                CMD.dir(in, out);
                break;
            case "echo":
                CMD.echo(in, out);
                break;
            case "environ":
                CMD.environ(out);
                break;
            case "exec":
                CMD.exec(in);
                break;
            case "fg":
                CMD.fg(in);
                break;
            case "time":
                CMD.time(out);
                break;
            case "exit":
                CMD.exit(in);
                break;
            case "jobs":
                CMD.jobs(out);
                break;
            case "pwd":
                CMD.pwd(out);
                break;
            case "quit":
                CMD.quit();
                break;
            case "set":
                CMD.set(in);
                break;
            case "shift":
                if (args.size() > 0) {
                    try {
                        int shamt = Integer.parseInt(args.get(0));
                        if (shamt >= 0) {
                            CMD.shift(in, out, shamt, input_type == IOType.ARGS_IN);
                        } else {
                            System.out.println("[RuntimeError] Shift amount out of range.");
                        }
                    } catch (Exception e) {
                        System.out.println("[RuntimeError] Cannot parse the shift amount.");
                    }
                } else {
                    System.out.println("[RuntimeError] Must give a shift amount.");
                }
                break;
            case "unset":
                CMD.unset(in);
                break;
            case "umask":
                CMD.umask(in, out);
                break;
            case "help":
                CMD.help(out);
                break;
            case "more help":
                CMD.more_help();
                break;
            case "test":
                if (args.size() > 0) {
                    if (args.get(0).equals("-z")) {
                        CMD.test(in, out, false, input_type == IOType.ARGS_IN);
                    } else if (args.get(0).equals("-n")) {
                        CMD.test(in, out, true, input_type == IOType.ARGS_IN);
                    } else {
                        System.out.println("[RuntimeError] Invalid test option: " + args.get(0));
                    }
                } else {
                    System.out.println("[RuntimeError] Must give an option of test command.");
                }
                break;
            case "myshell":
                CMD.myshell(in, out);
                break;
        }
    }
}
