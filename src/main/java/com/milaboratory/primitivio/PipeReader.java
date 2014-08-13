package com.milaboratory.primitivio;

import cc.redberry.pipe.OutputPortCloseable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PipeReader<O> extends PReader implements OutputPortCloseable<O> {
    final Class type;

    public PipeReader(Class<? super O> type, String fileName) throws IOException {
        super(fileName);
        this.type = type;
    }

    public PipeReader(Class<? super O> type, File file) throws IOException {
        super(file);
        this.type = type;
    }

    private PipeReader(Class<? super O> type, FileInputStream stream) throws IOException {
        super(stream);
        this.type = type;
    }

    public PipeReader(Class<? super O> type, InputStream stream) {
        super(stream);
        this.type = type;
    }

    @Override
    public synchronized O take() {
        if (closed.get())
            return null;

        return (O) input.readObject(type);
    }
}