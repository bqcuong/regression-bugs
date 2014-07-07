package com.milaboratory.core.io.sequence.fastq;

import cc.redberry.pipe.OutputPortCloseable;
import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.io.sequence.SingleReader;
import com.milaboratory.core.io.util.FileIndex;
import com.milaboratory.core.io.util.FileIndexBuilder;
import com.milaboratory.util.CanReportProgress;

public class SingleFastqIndexer implements SingleReader,
        CanReportProgress, OutputPortCloseable<SingleRead> {
    private final SingleFastqReader reader;
    private final FileIndexBuilder indexBuilder;

    public SingleFastqIndexer(SingleFastqReader reader, long step) {
        this.reader = reader;
        this.indexBuilder = new FileIndexBuilder(step);
    }

    public SingleFastqReader setTotalSize(long totalSize) {
        return reader.setTotalSize(totalSize);
    }

    @Override
    public double getProgress() {
        return reader.getProgress();
    }

    @Override
    public boolean isFinished() {
        return reader.isFinished();
    }

    @Override
    public SingleRead take() {
        SingleRead read = reader.take();
        if (read == null)
            return null;
        indexBuilder.appendNextRecord(reader.recordsReader.qualityEnd - reader.recordsReader.descriptionBegin + 2);
        return read;
    }

    @Override
    public void close() {
        reader.close();
    }

    public FileIndex createIndex() {
        indexBuilder.putMetadata("format", reader.getQualityFormat().toString());
        return indexBuilder.createAndDestroy();
    }

    public SingleFastqIndexer readToEnd() {
        while (take() != null) ;
        return this;
    }
}
