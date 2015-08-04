/*
 * Copyright 2015 MiLaboratory.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milaboratory.core.io.sequence.fasta;

import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.io.sequence.SingleReadImpl;
import com.milaboratory.core.io.sequence.SingleReader;
import com.milaboratory.core.sequence.NSequenceWithQuality;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.sequence.SequenceQuality;
import com.milaboratory.util.CanReportProgress;

/**
 * Converts {@link FastaReader}<{@link NucleotideSequence}> to
 * {@link com.milaboratory.core.io.sequence.SingleReader}.
 */
public class FastaSequenceReaderWrapper implements SingleReader, CanReportProgress {
    private final FastaReader<NucleotideSequence> internalReader;

    public FastaSequenceReaderWrapper(FastaReader<NucleotideSequence> internalReader) {
        this.internalReader = internalReader;
    }

    @Override
    public double getProgress() {
        return internalReader.getProgress();
    }

    @Override
    public boolean isFinished() {
        return internalReader.isFinished();
    }

    @Override
    public void close() {
        internalReader.close();
    }

    @Override
    public long getNumberOfReads() {
        return internalReader.getNumberOfReads();
    }

    @Override
    public SingleRead take() {
        FastaRecord<NucleotideSequence> record = internalReader.take();
        return new SingleReadImpl(record.getId(), new NSequenceWithQuality(record.getSequence(),
                SequenceQuality.getUniformQuality(SequenceQuality.GOOD_QUALITY_VALUE, record.getSequence().size())),
                record.getDescription());
    }
}
