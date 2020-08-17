package Runtime;

import Utilities.EType;
import Utilities.ShellException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;


public class Executor {
    private static final InputStream console_in = System.in;
    private static final PrintStream console_out = System.out;

    public static void ResetRedirect() {
        System.setIn(console_in);
        System.setOut(console_out);
    }

    public static LinkedHashMap<String, String> variables;

    public static void Init() {
        variables = new LinkedHashMap<>();
        variables.put("HOME", System.getProperty("user.home"));
        variables.put("PWD", System.getProperty("user.home"));
    }

    public static void SetupProcess(Command[] commands, int num_command)
            throws ShellException {
        if(commands[num_command-1].background) {
            try{
                Thread bg_process = new Thread(commands[num_command-1], commands[num_command-1].name);
                System.out.println("\rPID: " + bg_process.getId());
                bg_process.start();
            } catch (Exception e) {
                throw new ShellException(EType.RuntimeError, "Failed to create background process.");
            }
            num_command--;
        }

        for(int i=0; i<num_command; i++) {
            try {
                Thread current_process = new Thread(commands[i], commands[i].name);
                current_process.start();
                current_process.join();
            } catch (Exception e) {
                throw new ShellException(EType.RuntimeError, "Failed to create process.");
            }
        }
        Executor.ResetRedirect();
    }
}
