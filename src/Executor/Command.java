package Executor;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

public class Command implements Runnable{
    public String name;

    public ArrayList<String> args;

    public boolean background;

    public PipeIOType pipe_type;
    public PipedInputStream pipe_in;
    public PipedOutputStream pipe_out;

    public RedirectType redirect_in, redirect_out;
    public String infile, outfile;

    public Command() {
        name = "";
        args = new ArrayList<>();
        background = false;
        pipe_type = PipeIOType.NONE;
        pipe_in = new PipedInputStream();
        pipe_out = new PipedOutputStream();
        redirect_in = redirect_out = RedirectType.NONE;
        infile = outfile = "";
    }

    @Override
    public void run() {

    }
}
