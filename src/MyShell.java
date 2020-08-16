
import Interpreter.*;
import Runtime.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyShell {

    public static void main(String[] args) throws IOException {
        Environment.Init();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input_string;
        while(true) {
            Interpreter.Prompt();
            input_string = br.readLine();
            if(!input_string.equals("")) {
                Interpreter.ProcessCMD(input_string);
            }
        }
    }
}
