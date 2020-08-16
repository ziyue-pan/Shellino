package Utilities;

public class ShellException extends Exception{
    public String cmd;
    public String msg;
    public EType type;

    public ShellException(String cmd, EType type, String msg) {
        this.cmd = cmd;
        this.type = type;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return "[" + cmd + ": " + type.name() + "]" + msg;
    }
}
