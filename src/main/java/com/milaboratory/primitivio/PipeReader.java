package com.milaboratory.primitivio;

import cc.redberry.pipe.OutputPortCloseable;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PipeReader<O> implements OutputPortCloseable<O>, AutoCloseable {
    final Class type;
    final PrimitivI input;
    final AtomicBoolean closed = new AtomicBoolean(false);

    public PipeReader(Class<? super O> type, String fileName) throws FileNotFoundException {
        this(type, new BufferedInputStream(new FileInputStream(fileName), 32768));
    }

    public PipeReader(Class<? super O> type, File file) throws FileNotFoundException {
        this(type, new BufferedInputStream(new FileInputStream(file), 32768));
    }

    public PipeReader(Class<? super O> type, InputStream stream) {
        this.input = new PrimitivI(stream);
        this.type = type;
    }


    @Override
    public synchronized O take() {
        if (closed.get())
            return null;

        return (O) input.readObject(type);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            input.close();
        }
    }
}