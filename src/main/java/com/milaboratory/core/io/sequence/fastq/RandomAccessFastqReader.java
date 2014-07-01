package com.milaboratory.core.io.sequence.fastq;

import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.io.sequence.SingleReadImpl;
import com.milaboratory.core.io.sequence.SingleReader;
import com.milaboratory.core.io.util.AbstractRandomAccessReader;
import com.milaboratory.core.io.util.FileIndex;
import com.milaboratory.core.sequence.NSequenceWithQuality;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.sequence.SequenceQuality;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class RandomAccessFastqReader
        extends AbstractRandomAccessReader<SingleRead>
        implements SingleReader {
    private final QualityFormat qualityFormat;

    public RandomAccessFastqReader(FileIndex fileIndex, File file)
            throws FileNotFoundException {
        this(fileIndex, new RandomAccessFile(file, "r"));
    }

    public RandomAccessFastqReader(FileIndex fileIndex, RandomAccessFile file) {
        super(fileIndex, file);
        this.qualityFormat = QualityFormat.fromName(fileIndex.getMetadata("format"));
    }

    @Override
    protected SingleRead take0() {
        try {
            String description = file.readLine();
            if (description == null)//EOF
                return null;
            String seq = file.readLine();
            file.readLine();
            String quality = file.readLine();

            return new SingleReadImpl(currentRecordNumber,
                    new NSequenceWithQuality(new NucleotideSequence(seq),
                            new SequenceQuality(quality, qualityFormat)),
                    description);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
