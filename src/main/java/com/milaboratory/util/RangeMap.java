package com.milaboratory.util;

import com.milaboratory.core.Range;

import java.util.Comparator;
import java.util.TreeMap;

import static java.util.Map.Entry;

public final class RangeMap<T> {
    private final TreeMap<Range, T> map = new TreeMap<>(rangeComparator);

    public RangeMap() {
    }

    public Entry<Range, T> getEntryThatContains(Range range) {
        Entry<Range, T> ret = map.floorEntry(range);
        if (ret != null && ret.getKey().contains(range))
            return ret;
        return null;
    }

    public void put(Range range, T value) {
        if (findSingleIntersection(range) != null)
            throw new IntersectingRangesException();
        map.put(range, value);
    }

    public T remove(Range range) {
        return map.remove(range);
    }

    public Entry<Range, T> findSingleIntersection(Range range) {
        Entry<Range, T> tmp = map.floorEntry(range);
        Entry<Range, T> ret = null;
        if (tmp != null && tmp.getKey().intersectsWith(range))
            ret = tmp;
        tmp = map.higherEntry(range);
        if (tmp != null && tmp.getKey().intersectsWith(range))
            if (ret != null)
                throw new IllegalArgumentException("Several intersection hits");
            else
                ret = tmp;
        return ret;
    }

    public static final class IntersectingRangesException extends RuntimeException {
        public IntersectingRangesException() {
        }

        public IntersectingRangesException(String message) {
            super(message);
        }
    }

    private static final Comparator<Range> rangeComparator = new Comparator<Range>() {
        @Override
        public int compare(Range o1, Range o2) {
            int cmp;
            if ((cmp = Integer.compare(o1.getLower(), o2.getLower())) != 0)
                return cmp;

            return Integer.compare(o2.getUpper(), o1.getUpper());
        }
    };
}
