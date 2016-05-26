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
package com.milaboratory.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.milaboratory.core.io.binary.RangeSerializer;
import com.milaboratory.primitivio.annotations.Serializable;

import java.util.Comparator;

/**
 * This class represents a range of positions in a sequence (e.g. sub-sequence). Range can be <b>reversed</b> ({@code
 * from > to}), to represent reverse complement sub-sequence of a nucleotide sequence.
 *
 * <p><b>Main contract:</b> upper limit (with biggest value) is always exclusive, and lower is always inclusive.</p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
@Serializable(by = RangeSerializer.class)
public final class Range implements java.io.Serializable {
    static final long serialVersionUID = 1L;

    private final int lower;
    private final int upper;
    private final boolean reversed;

    public Range(int lower, int upper, boolean reversed) {
        if (lower > upper)
            throw new IllegalArgumentException();

        this.lower = lower;
        this.upper = upper;
        this.reversed = reversed;
    }

    @JsonCreator
    public Range(@JsonProperty("from") int from,
                 @JsonProperty("to") int to) {
        if (this.reversed = (from > to)) {
            this.upper = from;
            this.lower = to;
        } else {
            this.upper = to;
            this.lower = from;
        }
    }

    public Range expand(int offset) {
        return expand(offset, offset);
    }

    public Range expand(int leftOffset, int rightOffset) {
        return new Range(lower - leftOffset, upper + rightOffset, reversed);
    }

    /**
     * Returns {@literal true} if {@code length() == 0}.
     *
     * @return {@literal true} if {@code length() == 0}.
     */
    public boolean isEmpty() {
        return upper == lower;
    }

    /**
     * Returns the length of this range.
     *
     * @return length of this range
     */
    public int length() {
        return upper - lower;
    }

    /**
     * Returns true if this range is reversed.
     *
     * @return true if this range is reversed
     */
    public boolean isReverse() {
        return reversed;
    }

    /**
     * Returns from value. This bound may be exclusive of inclusive depending on the range orientation (see main
     * contract in the class description).
     *
     * @return from value (exclusive or inclusive)
     */
    @JsonProperty("from")
    public int getFrom() {
        return reversed ? upper : lower;
    }

    /**
     * Returns to value. This bound may be exclusive of inclusive depending on the range orientation (see main contract
     * in the class description).
     *
     * @return to value (exclusive or inclusive)
     */
    @JsonProperty("to")
    public int getTo() {
        return reversed ? lower : upper;
    }

    /**
     * Returns upper (with biggest value) bound of this range. This bound is always exclusive.
     *
     * @return upper limit of this range (exclusive)
     */
    public int getUpper() {
        return upper;
    }

    /**
     * Returns lower (with least value) bound of this range. This bound is always inclusive.
     *
     * @return lower limit of this range (inclusive)
     */
    public int getLower() {
        return lower;
    }

    /**
     * Returns reversed range.
     *
     * @return reversed range
     */
    public Range inverse() {
        return new Range(lower, upper, !reversed);
    }

    /**
     * Returns {@code true} if range contains provided {@code position}.
     *
     * @param position position
     * @return {@code true} if range contains provided {@code position}
     */
    public boolean contains(int position) {
        return position >= lower && position < upper;
    }

    public boolean containsBoundary(int position) {
        return position >= lower && position <= upper;
    }

    /**
     * Returns {@code true} if range contains {@code other} range.
     *
     * @param other other range
     * @return {@code true} if range contains {@code other} range
     */
    public boolean contains(Range other) {
        return lower <= other.lower && upper >= other.upper;
    }

    /**
     * Returns {@code true} if range intersects with {@code other} range.
     *
     * @param other other range
     * @return {@code true} if range intersects with {@code other} range
     */
    public boolean intersectsWith(Range other) {
        return (this.contains(other.lower) && !other.isEmpty())
                || (other.contains(this.lower) && !this.isEmpty())
                || (other.upper > upper && other.lower < lower);
    }

    /**
     * Returns {@code true} if range intersects with {@code other} range.
     *
     * @param other other range
     * @return {@code true} if range intersects with {@code other} range
     */
    public boolean intersectsWithOrTouches(Range other) {
        return contains(other.lower) || contains(other.upper - 1) || (other.upper > upper && other.lower < lower) ||
                other.lower == upper || other.upper == lower;
    }


    /**
     * Returns intersection range with {@code other} range.
     *
     * @param other other range
     * @return intersection range with {@code other} range or null if ranges not intersects
     */
    public Range intersection(Range other) {
        if (!intersectsWith(other))
            return null;

        return new Range(Math.max(lower, other.lower), Math.min(upper, other.upper), reversed && other.reversed);
    }

    /**
     * Returns intersection range with {@code other} range.
     *
     * @param other other range
     * @return intersection range with {@code other} range or null if ranges not intersects ot touches
     */
    public Range tryMerge(Range other) {
        if (!intersectsWithOrTouches(other))
            return null;

        return new Range(Math.min(lower, other.lower), Math.max(upper, other.upper), reversed && other.reversed);
    }

    /**
     * Returns range moved using provided offset (e.g. [lower + offset, upper + offset, reversed])
     *
     * @param offset offset, can be negative
     * @return range moved using provided offset
     */
    public Range move(int offset) {
        if (offset == 0)
            return this;
        return new Range(lower + offset, upper + offset, reversed);
    }

    /**
     * Returns relative point position inside this range.
     *
     * @param absolutePosition absolute point position (in the same coordinates as this range boundaries)
     * @return relative point position inside this range
     */
    public int convertPointToRelativePosition(int absolutePosition) {
        if (absolutePosition < lower || absolutePosition >= upper)
            throw new IllegalArgumentException("Position outside this range (" + absolutePosition + ").");

        if (reversed)
            return upper - 1 - absolutePosition;
        else
            return absolutePosition - lower;
    }

    /**
     * Returns relative boundary position inside this range.
     *
     * @param absolutePosition absolute boundary position (in the same coordinates as this range boundaries)
     * @return relative boundary position inside this range
     */
    public int convertBoundaryToRelativePosition(int absolutePosition) {
        if (absolutePosition < lower || absolutePosition > upper)
            throw new IllegalArgumentException("Position outside this range (" + absolutePosition + ") this=" + this + ".");

        if (reversed)
            return upper - absolutePosition;
        else
            return absolutePosition - lower;
    }

    public Range getRelativeRangeOf(Range range) {
        int from = convertBoundaryToRelativePosition(range.getFrom()),
                to = convertBoundaryToRelativePosition(range.getTo());
        if (from == -1 || to == -1)
            return null;
        return new Range(from, to);
    }

    public int[] convertBoundariesToRelativePosition(int... absolutePositions) {
        int[] result = new int[absolutePositions.length];

        for (int i = 0; i < absolutePositions.length; ++i)
            result[i] = convertBoundaryToRelativePosition(absolutePositions[i]);

        return result;
    }

    public int[] convertPointsToRelativePosition(int... absolutePositions) {
        int[] result = new int[absolutePositions.length];

        for (int i = 0; i < absolutePositions.length; ++i)
            result[i] = convertPointToRelativePosition(absolutePositions[i]);

        return result;
    }

    /**
     * Converts relative point position to absolute position
     *
     * @param relativePosition relative point position
     * @return absolute point position
     */
    public int convertPointToAbsolutePosition(int relativePosition) {
        if (relativePosition < 0 || relativePosition >= length())
            throw new IllegalArgumentException("Relative position outside this range (" + relativePosition + ").");

        if (reversed)
            return upper - 1 - relativePosition;
        else
            return relativePosition + lower;
    }

    /**
     * Converts relative boundary position to absolute position
     *
     * @param relativePosition relative boundary position
     * @return absolute point position
     */
    public int convertBoundaryToAbsolutePosition(int relativePosition) {
        if (relativePosition < 0 || relativePosition > length())
            throw new IllegalArgumentException("Relative position outside this range (" + relativePosition + ").");

        if (reversed)
            return upper - relativePosition;
        else
            return relativePosition + lower;
    }

    @Override
    public String toString() {
        return "(" + lower + (reversed ? "<-" : "->") + upper + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        return lower == range.lower && reversed == range.reversed && upper == range.upper;
    }

    @Override
    public int hashCode() {
        int result = lower;
        result = 31 * result + upper;
        result = 31 * result + (reversed ? 1 : 0);
        return result;
    }

    public static final Comparator<Range> COMPARATOR_BY_FROM = new Comparator<Range>() {
        @Override
        public int compare(Range o1, Range o2) {
            return Integer.compare(o1.getFrom(), o2.getTo());
        }
    };
}
