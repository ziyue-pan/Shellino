package Interpreter;

import Runtime.*;
import Utilities.*;

import java.io.IOException;

public class Interpreter {
    public static boolean input_ready = false;
    public static State state = State.IDLE;
    public static Command[] tmp_commands;
    public static int command_num;

    public static void Prompt() {
        Common.Print(Color.RED_BOLD, Executor.SHELL_NAME);
        Common.Print(Color.GREEN, " → ");
        Common.Print(Color.CYAN_BOLD, Executor.variables.get("PWD"));
        Common.Print(" $ ");
        input_ready = true;
    }

    public static void ProcessCMD(String input_string)
            throws ShellException {
        String[] split_string = input_string.split("\\|");
        command_num = split_string.length;
        tmp_commands = new Command[command_num];

        for (int i = 0; i < command_num; i++) {
            tmp_commands[i] = new Command();
            ProcessSingle(i, split_string[i].trim());

            if (tmp_commands[i].name.equals(""))
                throw new ShellException(EType.SyntaxError, "Cannot parse empty command");
            if (tmp_commands[i].background && i < command_num - 1)
                throw new ShellException(EType.SyntaxError, "Cannot backgrounding commands between pipes");
            if (i > 0 && tmp_commands[i].input_type == IOType.REDIRECT_IN)
                throw new ShellException(EType.SyntaxError, "Cannot input from pipe and redirection at the same time");
            if (i < command_num - 1 && (tmp_commands[i].output_type == IOType.REDIRECT_OUT
                    || tmp_commands[i].output_type == IOType.REDIRECT_APPEND))
                throw new ShellException(EType.SyntaxError, "Cannot output to pipe and redirection at the same time");

            if (i == 0 && command_num > 1)
                tmp_commands[i].output_type = IOType.PIPE_OUT;
            if (i > 0) {
                if (i < command_num - 1) {
                    tmp_commands[i].input_type = IOType.PIPE_IN;
                    tmp_commands[i].output_type = IOType.PIPE_OUT;
                }
                else
                    tmp_commands[i].input_type = IOType.PIPE_IN;
                try {
                    tmp_commands[i].pipe_in.connect(tmp_commands[i - 1].pipe_out);
                } catch (IOException e) {
                    throw new ShellException(EType.RuntimeError,
                            "Cannot create pipes between " + tmp_commands[i - 1].name + " and " + tmp_commands[i].name);
                }
            }

            if (tmp_commands[i].background && i < command_num - 1)
                throw new ShellException(EType.SyntaxError, "Cannot set background command between pipes");
        }

        Executor.SetupProcess(tmp_commands, command_num);
    }

    public static void ProcessSingle(int idx, String single_string)
            throws ShellException {
        String[] tokens = single_string.split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            switch (state) {
                case IDLE: {
                    if (Data.supported_commands.contains(tokens[i])) {
                        tmp_commands[idx].name = tokens[i];
                        state = State.COMMAND_PARSED;
                    } else {
                        throw new ShellException(
                                EType.SyntaxError, "Invalid Command: " + tokens[i]);
                    }
                }
                break;
                case COMMAND_PARSED: {
                    switch (tokens[i]) {
                        case "<":
                            if (i < tokens.length - 1) {
                                tmp_commands[idx].input_type = IOType.REDIRECT_IN;
                                tmp_commands[idx].infile = tokens[i + 1];
                                i++;
                                state = State.REDIRECT_IN_PARSED;
                            } else {
                                throw new ShellException(
                                        EType.SyntaxError, "Missing path for redirect-in file");
                            }
                            break;
                        case ">":
                            if (i < tokens.length - 1) {
                                tmp_commands[idx].output_type = IOType.REDIRECT_OUT;
                                tmp_commands[idx].outfile = tokens[i + 1];
                                i++;
                                state = State.REDIRECT_OUT_PARSED;
                            } else {
                                throw new ShellException(
                                        EType.SyntaxError, "Missing path for redirect-out file");
                            }
                            break;
                        case ">>":
                            if (i < tokens.length - 1) {
                                tmp_commands[idx].output_type = IOType.REDIRECT_APPEND;
                                tmp_commands[idx].outfile = tokens[i + 1];
                                i++;
                                state = State.REDIRECT_OUT_PARSED;
                            } else {
                                throw new ShellException(
                                        EType.SyntaxError, "Missing path for redirect-out file");
                            }
                            break;
                        case "&":
                            tmp_commands[idx].background = true;
                            state = State.AMPERSAND_PARSED;
                            break;
                        default:
                            tmp_commands[idx].args.add(tokens[i]);
                            break;
                    }
                }
                break;
                case REDIRECT_IN_PARSED:
                    switch (tokens[i]) {
                        case ">":
                            if (i < tokens.length - 1) {
                                tmp_commands[idx].output_type = IOType.REDIRECT_OUT;
                                tmp_commands[idx].outfile = tokens[i + 1];
                                i++;
                                state = State.REDIRECT_BOTH_PARSED;
                            } else {
                                throw new ShellException(
                                        EType.SyntaxError, "Missing path for redirect-out file");
                            }
                            break;
                        case ">>":
                            if (i < tokens.length - 1) {
                                tmp_commands[idx].output_type = IOType.REDIRECT_APPEND;
                                tmp_commands[idx].outfile = tokens[i + 1];
                                i++;
                                state = State.REDIRECT_BOTH_PARSED;
                            } else {
                                throw new ShellException(
                                        EType.SyntaxError, "Missing path for redirect-out file");
                            }
                            break;
                        case "&":
                            tmp_commands[idx].background = true;
                            state = State.AMPERSAND_PARSED;
                            break;
                        default:
                            throw new ShellException(
                                    EType.SyntaxError, "Invalid token after redirection: " + tokens[i]);
                    }
                    break;
                case REDIRECT_OUT_PARSED:
                    switch (tokens[i]) {
                        case "<":
                            if (i < tokens.length - 1) {
                                tmp_commands[idx].input_type = IOType.REDIRECT_IN;
                                tmp_commands[idx].infile = tokens[i + 1];
                                i++;
                                state = State.REDIRECT_BOTH_PARSED;
                            } else {
                                throw new ShellException(
                                        EType.SyntaxError, "Missing path for redirect-in file");
                            }
                            break;
                        case "&":
                            tmp_commands[idx].background = true;
                            state = State.AMPERSAND_PARSED;
                            break;
                        default:
                            throw new ShellException(
                                    EType.SyntaxError, "Invalid token after redirection: " + tokens[i]);
                    }
                    break;
                case AMPERSAND_PARSED:
                    throw new ShellException(EType.SyntaxError, "Invalid token: " + tokens[i]);
            }
        }
        state = State.IDLE;
    }

    public static void Reset() {
        state = State.IDLE;
        command_num = 0;
        tmp_commands = null;
    }
}
