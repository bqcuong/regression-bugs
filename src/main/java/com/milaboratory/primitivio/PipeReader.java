package com.milaboratory.primitivio;

import cc.redberry.pipe.OutputPortCloseable;
import com.milaboratory.util.CanReportProgress;
import com.milaboratory.util.CountingInputStream;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PipeReader<O> implements OutputPortCloseable<O>, AutoCloseable, CanReportProgress {
    final Class type;
    final PrimitivI input;
    final AtomicBoolean closed = new AtomicBoolean(false);
    final CountingInputStream countingInputStream;
    final long totalSize;

    public PipeReader(Class<? super O> type, String fileName) throws IOException {
        this(type, new FileInputStream(fileName));
    }

    public PipeReader(Class<? super O> type, File file) throws IOException {
        this(type, new FileInputStream(file));
    }

    private PipeReader(Class<? super O> type, FileInputStream stream) throws IOException {
        this.countingInputStream = new CountingInputStream(new BufferedInputStream(stream, 32768));
        this.input = new PrimitivI(this.countingInputStream);
        this.type = type;
        this.totalSize = stream.getChannel().size();
    }

    public PipeReader(Class<? super O> type, InputStream stream) {
        this(type, stream, -1);
    }

    public PipeReader(Class<? super O> type, InputStream stream, long totalSize) {
        this.countingInputStream = new CountingInputStream(stream);
        this.input = new PrimitivI(this.countingInputStream);
        this.type = type;
        this.totalSize = totalSize;
    }

    @Override
    public synchronized O take() {
        if (closed.get())
            return null;

        return (O) input.readObject(type);
    }

    @Override
    public double getProgress() {
        return 1.0 * countingInputStream.getBytesRead() / totalSize;
    }

    @Override
    public boolean isFinished() {
        return closed.get();
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            input.close();
        }
    }
}