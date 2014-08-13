package com.milaboratory.primitivio;

import cc.redberry.pipe.InputPort;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

public class PipeWriter<O> extends PWriter implements InputPort<O>, AutoCloseable {
    public PipeWriter(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public PipeWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public PipeWriter(OutputStream stream) {
        super(stream);
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
    protected void beforeClose() {
        output.writeObject(null);
    }
}