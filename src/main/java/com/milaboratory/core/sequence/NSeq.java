package com.milaboratory.core.sequence;

public interface NSeq<S extends NSeq<S>> extends Seq<S> {
    S getReverseComplement();
}
