package Utilities;

public class ShellException extends Exception{
    public String msg;
    public EType type;

    public ShellException(EType type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return "[" +type.name() + "] " + msg;
    }
}
