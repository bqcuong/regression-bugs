package com.milaboratory.core.alignment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;

/**
 * AlignmentScoring - interface which is to be implemented by any scoring system
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = LinearGapAlignmentScoring.class, name = "linear"),
        @JsonSubTypes.Type(value = AffineGapAlignmentScoring.class, name = "affine")})
public interface AlignmentScoring<S extends Sequence<S>> {
    int getScore(byte from, byte to);

    Alphabet<S> getAlphabet();
}
