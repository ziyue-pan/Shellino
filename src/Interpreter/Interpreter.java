package Interpreter;

import Utilities.*;
import Runtime.*;

public class Interpreter {
    public static void Prompt() {
        Common.Print(Color.RED_BOLD, Settings.SHELL_NAME);
        Common.Print(Color.GREEN, " → ");
        Common.Print(Color.CYAN_BOLD, Environment.Variables.get("PWD"));
        Common.Print(" $ ");
    }

    public static void ProcessCMD(String input_string) {
        System.out.println(input_string);
    }




}
