package Interpreter;

public enum State {
    IDLE,
    COMMAND_PARSED,
    REDIRECT_IN_PARSED,
    REDIRECT_OUT_PARSED,
    REDIRECT_BOTH_PARSED,
    AMPERSAND_PARSED
}
