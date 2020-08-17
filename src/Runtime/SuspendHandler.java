package Runtime;

import Interpreter.Data;
import Utilities.ShellException;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class SuspendHandler implements SignalHandler {
    @Override
    public void handle(Signal signal) {
        long current_thread_id = Thread.currentThread().getId();
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (Data.supported_commands.contains(t.getName()) && t.getId() == current_thread_id && t.isAlive()) {
                try {
                    t.wait();
                } catch (InterruptedException e) {
                    System.out.println("[RuntimeError] " + e.getMessage());
                }
            }
        }
    }
}
