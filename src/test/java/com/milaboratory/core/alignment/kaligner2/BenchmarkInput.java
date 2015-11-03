package com.milaboratory.core.alignment.kaligner2;

/**
 * Created by dbolotin on 27/10/15.
 */
public final class BenchmarkInput {
    public final KAlignerParameters2 params;
    public final Challenge challenge;

    public BenchmarkInput(KAlignerParameters2 params, Challenge challenge) {
        this.params = params;
        this.challenge = challenge;
    }
}
