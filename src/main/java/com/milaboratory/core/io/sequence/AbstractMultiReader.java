package com.milaboratory.core.io.sequence;

import cc.redberry.pipe.OutputPortCloseable;
import com.milaboratory.util.CanReportProgress;

/**
 * Created by dbolotin on 23/06/14.
 */
public abstract class AbstractMultiReader<R extends SequenceRead> implements CanReportProgress, SequenceReader<R>, OutputPortCloseable<R> {
    private final SingleReader[] readers;
    private final CanReportProgress[] progressReporters;

    public AbstractMultiReader(SingleReader... readers) {
        for (SingleReader reader : readers)
            if (reader == null)
                throw new NullPointerException();

        this.readers = readers;
        boolean crp = true;
        for (SingleReader reader : readers)
            crp &= reader instanceof CanReportProgress;
        if (crp) {
            progressReporters = new CanReportProgress[readers.length];
            for (int i = 0; i < readers.length; i++)
                progressReporters[i] = (CanReportProgress) readers[i];
        } else
            progressReporters = null;

    }

    protected synchronized SingleRead[] takeReads() {
        SingleRead[] reads = new SingleRead[readers.length];

        boolean hasNulls = false, allNulls = true;
        for (int i = 0; i < reads.length; i++) {
            reads[i] = readers[i].take();
            hasNulls |= (reads[i] == null);
            allNulls &= (reads[i] == null);
        }

        if (allNulls)
            return null;

        if (hasNulls)
            throw new RuntimeException("Different number of reads in single-readers.");

        return reads;
    }

    @Override
    public void close() {
        RuntimeException exception = null;

        for (SingleReader reader : readers)
            if (reader instanceof OutputPortCloseable)
                try {
                    ((OutputPortCloseable) reader).close();
                } catch (RuntimeException e) {
                    exception = e;
                }

        if (exception != null)
            throw exception;
    }

    @Override
    public double getProgress() {
        if (progressReporters == null)
            return Double.NaN;

        double sum = 0.0;
        for (CanReportProgress reporter : progressReporters) {
            double progress = reporter.getProgress();
            if (Double.isNaN(progress))
                return Double.NaN;
            sum += progress;
        }

        return sum / progressReporters.length;
    }

    @Override
    public boolean isFinished() {
        if (progressReporters == null)
            return true;

        boolean allFinished = true;
        for (CanReportProgress reporter : progressReporters)
            allFinished &= reporter.isFinished();

        return allFinished;
    }
}
