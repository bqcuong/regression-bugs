package com.milaboratory.primitivio;

import cc.redberry.pipe.InputPort;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PipeWriter<O> implements InputPort<O>, AutoCloseable {
    final PrimitivO output;
    final AtomicBoolean closed = new AtomicBoolean(false);

    public PipeWriter(String fileName) throws FileNotFoundException {
        this(new BufferedOutputStream(new FileOutputStream(fileName), 32768));
    }

    public PipeWriter(File file) throws FileNotFoundException {
        this(new BufferedOutputStream(new FileOutputStream(file), 32768));
    }

    public PipeWriter(OutputStream stream) {
        output = new PrimitivO(stream);
    }

    @Override
    public synchronized void put(O o) {
        if (o == null){
            close();
            return;
        }

        if (closed.get())
            throw new IllegalStateException("Already closed.");

        output.writeObject(o);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            output.writeObject(null);
            output.close();
        }
    }
}