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
package com.milaboratory.core.sequence;

import com.milaboratory.core.Range;
import com.milaboratory.primitivio.annotations.Serializable;

/**
 * Container of nucleotide sequence with its quality.
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @see com.milaboratory.core.sequence.SequenceWithQuality
 */
@Serializable(by = IO.NSequenceWithQualitySerializer.class)
public final class NSequenceWithQuality extends SequenceWithQuality<NucleotideSequence>
        implements NSeq<NSequenceWithQuality> {
    /**
     * Creates nucleotide sequence with its quality
     *
     * @param sequence nucleotide sequence
     * @param quality  quality
     * @throws java.lang.IllegalArgumentException if {@code sequence.size() != quality.size()}
     */
    public NSequenceWithQuality(NucleotideSequence sequence, SequenceQuality quality) {
        super(sequence, quality);
    }

    public NSequenceWithQuality(String sequence, String quality) {
        super(new NucleotideSequence(sequence), new SequenceQuality(quality));
    }

    /**
     * Returns reverse complement sequence with reversed quality.
     *
     * @return reverse complement sequence with reversed quality
     */
    @Override
    public NSequenceWithQuality getReverseComplement() {
        return new NSequenceWithQuality(sequence.getReverseComplement(), quality.reverse());
    }

    @Override
    public NSequenceWithQuality getRange(int from, int to) {
        return new NSequenceWithQuality(sequence.getRange(from, to), quality.getRange(from, to));
    }

    @Override
    public NSequenceWithQuality getRange(Range range) {
        return new NSequenceWithQuality(sequence.getRange(range), quality.getRange(range));
    }

    @Override
    public NSequenceWithQualityBuilder getBuilder() {
        return new NSequenceWithQualityBuilder();
    }

    @Override
    public NSequenceWithQuality concatenate(NSequenceWithQuality other) {
        return getBuilder()
                .ensureCapacity(other.size() + size())
                .append(this).append(other).createAndDestroy();
    }
}
