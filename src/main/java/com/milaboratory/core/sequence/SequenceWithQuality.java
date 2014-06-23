package com.milaboratory.core.sequence;

import com.milaboratory.core.Range;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class SequenceWithQuality<S extends Sequence<S>> {
    final S sequence;
    final SequenceQuality quality;

    public SequenceWithQuality(S sequence, SequenceQuality quality) {
        if (sequence.size() != quality.size())
            throw new IllegalArgumentException("Different sizes.");
        this.sequence = sequence;
        this.quality = quality;
    }

    public S getSequence() {
        return sequence;
    }

    public SequenceQuality getQuality() {
        return quality;
    }

    /**
     * Generates a new instance of NucleotideSQPair containing sub sequence. If to &lt; from then reverse complement
     * will be returned.
     *
     * @param from inclusive
     * @param to   exclusive
     */
    public SequenceWithQuality<S> getRange(int from, int to) {
        return getRange(new Range(from, to));
    }

    public SequenceWithQuality<S> getRange(Range range) {
        return new SequenceWithQuality<>(sequence.getSubSequence(range), quality.getRange(range));
    }

    public int size() {
        return sequence.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SequenceWithQuality that = (SequenceWithQuality) o;

        return sequence.equals(that.sequence)
                && quality.equals(that.quality);
    }

    @Override
    public int hashCode() {
        int result = sequence.hashCode();
        result = 31 * result + quality.hashCode();
        return result;
    }

    public String toPrettyString() {
        String seq = sequence.toString();
        String qual = quality.toString();
        return seq + '\n' + qual;
    }

    @Override
    public String toString() {
        return sequence.toString() + " " +
                quality.toString();
    }
}
