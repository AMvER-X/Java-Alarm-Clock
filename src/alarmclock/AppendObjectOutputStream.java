package alarmclock;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

class AppendObjectOutputStream extends ObjectOutputStream {
    public AppendObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        reset();
    }
}