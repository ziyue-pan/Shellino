package Runtime;

import Utilities.EType;
import Utilities.ShellException;
import sun.misc.Signal;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;


public class Executor {
    public static String SHELL_NAME = "Shellino"; // SHELL的名称
    private static final InputStream console_in = System.in;
    private static final PrintStream console_out = System.out;

    public static void ResetRedirect() {
        System.setIn(console_in);
        System.setOut(console_out);
    }

    public static LinkedHashMap<String, String> variables;

    public static void Init() {
        SuspendHandler handler = new SuspendHandler();
        // Signal.handle(new Signal("TSTP"), handler);

        variables = new LinkedHashMap<>();
        variables.put("SHELL", System.getProperty("user.dir") + "/Shellino.jar");
        variables.put("HOME", System.getProperty("user.home"));
        variables.put("PWD", System.getProperty("user.home"));
        variables.put("UMASK", "022");
    }

    public static void SetupProcess(Command[] commands, int num_command)
            throws ShellException {
        if (commands[num_command - 1].background) {
            try {
                Thread bg_process = new Thread(commands[num_command - 1], commands[num_command - 1].name);
                System.out.println("\rPID: " + bg_process.getId());
                bg_process.start();
            } catch (Exception e) {
                throw new ShellException(EType.RuntimeError, "Failed to create background process.");
            }
            num_command--;
        }

        Thread[] current_process_queue = new Thread[num_command];
        for (int i = 0; i < num_command; i++) {
            try {
                current_process_queue[i] = new Thread(commands[i], commands[i].name);
            } catch (Exception e) {
                throw new ShellException(EType.RuntimeError, "Failed to create process.");
            }
        }

        for (int i = 0; i < num_command; i++) {

            try {
                if (!commands[i].running) {
                    current_process_queue[i].start();
                    current_process_queue[i].join();
                }
            } catch (Exception e) {
                throw new ShellException(EType.RuntimeError, "Failed to start process.");
            }
        }
    }
}
