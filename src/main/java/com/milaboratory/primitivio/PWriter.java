package com.milaboratory.primitivio;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PWriter implements AutoCloseable {
    protected final PrimitivO output;
    protected final AtomicBoolean closed = new AtomicBoolean(false);

    protected PWriter(String fileName) throws FileNotFoundException {
        this(new BufferedOutputStream(new FileOutputStream(fileName), 32768));
    }

    protected PWriter(File file) throws FileNotFoundException {
        this(new BufferedOutputStream(new FileOutputStream(file), 32768));
    }

    protected PWriter(OutputStream stream) {
        this(new PrimitivO(stream));
    }

    protected PWriter(PrimitivO output) {
        this.output = output;
    }

    protected void beforeClose() {
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            beforeClose();
            output.close();
        }
    }
}
