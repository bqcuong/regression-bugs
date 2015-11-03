package com.milaboratory.core.alignment.kaligner2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BenchmarkResults {
    @JsonIgnore
    public final BenchmarkInput input;
    public final KAligner2Statistics stat;
    public final long executionTime;
    public final int processedQueries;
    public final int mismatched;
    public final int noHits;

    public BenchmarkResults(BenchmarkInput input, KAligner2Statistics stat,
                            long executionTime, int processedQueries, int mismatched, int noHits) {
        this.input = input;
        this.stat = stat;
        this.executionTime = executionTime;
        this.processedQueries = processedQueries;
        this.mismatched = mismatched;
        this.noHits = noHits;
    }

    public BenchmarkInput getInput() {
        return input;
    }

    public KAligner2Statistics getStat() {
        return stat;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getProcessedQueries() {
        return processedQueries;
    }

    public int getMismatched() {
        return mismatched;
    }

    public int getNoHits() {
        return noHits;
    }

    public double getNoHitsFraction() {
        return 1.0 * noHits / processedQueries;
    }

    public double getMismatchedFraction() {
        return 1.0 * mismatched / processedQueries;
    }

    public double getBadFraction() {
        return 1.0 * (noHits + mismatched) / processedQueries;
    }

    public long getAverageTiming() {
        return executionTime / processedQueries;
    }
}
