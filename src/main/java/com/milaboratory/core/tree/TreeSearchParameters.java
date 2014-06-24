package com.milaboratory.core.tree;

public final class TreeSearchParameters {
    public static final TreeSearchParameters ONE_MISMATCH = new TreeSearchParameters(1, 0, 0, 1);
    public static final TreeSearchParameters ONE_INDEL = new TreeSearchParameters(0, 1, 1, 1);
    public static final TreeSearchParameters ONE_MISMATCH_OR_INDEL = new TreeSearchParameters(1, 1, 1, 1);
    public static final double[] DEFAULT_PENALTY = {
            0.1, // Mismatch penalty
            0.21, // Deletion penalty
            0.32}; // Insertion penalty

    private static final double UNIFORM_PENALTY_VALUE = 1;
    private static final double[] UNIFORM_PENALTY = {
            UNIFORM_PENALTY_VALUE, // Mismatch penalty
            UNIFORM_PENALTY_VALUE, // Deletion penalty
            UNIFORM_PENALTY_VALUE}; // Insertion penalty

    private final int[] maxErrors;
    private final double[] penalty;
    private final double maxPenalty;
    private final byte[][] differencesCombination;

    public TreeSearchParameters(int[] maxErrors, double[] penalty, double maxPenalty) {
        if (penalty.length != 3 || maxErrors.length != 3)
            throw new IllegalArgumentException();
        this.maxErrors = maxErrors.clone();
        this.penalty = penalty.clone();
        this.maxPenalty = maxPenalty;
        this.differencesCombination = PenaltyUtils.getDifferencesCombination(maxPenalty, penalty, maxErrors);
    }

    /**
     * Parameters to search with limited number of each mutation type.
     *
     * <p>Ordering of search is according to {@link #DEFAULT_PENALTY}.</p>
     *
     * @param mismatches maximum number of mismatches
     * @param deletions  maximum number of deletions
     * @param insertions maximum number of insertions
     */
    public TreeSearchParameters(int mismatches, int deletions, int insertions) {
        this(new int[]{mismatches, deletions, insertions},
                DEFAULT_PENALTY,
                maxPenaltyFor(mismatches, deletions, insertions));
    }

    public TreeSearchParameters(int mismatches, int deletions, int insertions,
                                int totalErrors) {
        this(new int[]{mismatches, deletions, insertions}, UNIFORM_PENALTY, UNIFORM_PENALTY_VALUE * totalErrors);
    }

    public TreeSearchParameters(int maxSubstitutions, int maxDeletions, int maxInsertions,
                                double substitutionPenalty, double deletionPenalty, double insertionPenalty,
                                double maxPenalty) {
        this.maxErrors = new int[]{maxSubstitutions, maxDeletions, maxInsertions};
        this.penalty = new double[]{substitutionPenalty, deletionPenalty, insertionPenalty};
        this.maxPenalty = maxPenalty;
        this.differencesCombination = PenaltyUtils.getDifferencesCombination(maxPenalty, penalty, maxErrors);
    }

    private static double maxPenaltyFor(int mismatches, int deletions, int insertions) {
        double maxPenalty = .1;
        maxPenalty += mismatches * DEFAULT_PENALTY[0];
        maxPenalty += deletions * DEFAULT_PENALTY[1];
        maxPenalty += insertions * DEFAULT_PENALTY[2];
        return maxPenalty;
    }

    public int getMaxErrors(int errorType) {
        return maxErrors[errorType];
    }

    public double getPenalty(int errorType) {
        return penalty[errorType];
    }

    public int getMaxSubstitutions() {
        return maxErrors[0];
    }

    public int getMaxDeletions() {
        return maxErrors[1];
    }

    public int getMaxInsertions() {
        return maxErrors[2];
    }

    public double getSubstitutionPenalty() {
        return penalty[0];
    }

    public double getDeletionPenalty() {
        return penalty[1];
    }

    public double getInsertionPenalty() {
        return penalty[2];
    }

    public double getMaxPenalty() {
        return maxPenalty;
    }

    byte[][] getDifferencesCombination() {
        return differencesCombination;
    }
}
