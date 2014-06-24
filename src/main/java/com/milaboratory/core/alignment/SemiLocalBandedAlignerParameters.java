package com.milaboratory.core.alignment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class SemiLocalBandedAlignerParameters {
    LinearGapAlignmentScoring scoring;
    int width;
    int stopPenalty;

    @JsonCreator
    public SemiLocalBandedAlignerParameters(@JsonProperty("scoring") LinearGapAlignmentScoring scoring,
                                            @JsonProperty("width") int width,
                                            @JsonProperty("stopPenalty") int stopPenalty) {
        this.scoring = scoring;
        this.width = width;
        this.stopPenalty = stopPenalty;
    }

    /**
     * Returns scoring used for alignment.
     */
    public LinearGapAlignmentScoring getScoring() {
        return scoring;
    }

    /**
     * Sets scoring used for alignment.
     */
    public SemiLocalBandedAlignerParameters setScoring(LinearGapAlignmentScoring scoring) {
        this.scoring = scoring;
        return this;
    }

    /**
     * Width of banded alignment matrix. This value affects maximal possible number of indels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets width of banded alignment matrix. This value is connected to max allowed number of indels.
     */
    public SemiLocalBandedAlignerParameters setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * Alignment score value in banded alignment matrix at which alignment terminates.
     */
    public int getStopPenalty() {
        return stopPenalty;
    }

    /**
     * Sets alignment score value in banded alignment matrix at which alignment terminates.
     */
    public SemiLocalBandedAlignerParameters setStopPenalty(int stopPenalty) {
        this.stopPenalty = stopPenalty;
        return this;
    }

    public SemiLocalBandedAlignerParameters clone() {
        return new SemiLocalBandedAlignerParameters(scoring, width, stopPenalty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemiLocalBandedAlignerParameters that = (SemiLocalBandedAlignerParameters) o;

        if (stopPenalty != that.stopPenalty) return false;
        if (width != that.width) return false;
        if (!scoring.equals(that.scoring)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = scoring.hashCode();
        result = 31 * result + width;
        result = 31 * result + stopPenalty;
        return result;
    }
}
