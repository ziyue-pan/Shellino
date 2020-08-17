
import Runtime.Executor;
import Interpreter.*;
import Utilities.ShellException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyShell {
    public static void main(String[] args) throws IOException {
        Executor.Init();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input_string;
        while(true) {
            Interpreter.Prompt();
            input_string = br.readLine();
            Interpreter.input_ready = false;
            if(!input_string.equals("")) {
                try {
                    Interpreter.ProcessCMD(input_string);
                } catch (ShellException e) {
                    Interpreter.state = State.IDLE;
                    Interpreter.Reset();
                    Executor.ResetRedirect();
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
