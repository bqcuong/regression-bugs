package com.milaboratory.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class RangeTest {
    private Range r(int from, int to) {
        return new Range(from, to);
    }

    @Test
    public void testValues() throws Exception {
        assertEquals(20, r(20, 10).getUpper());
        assertEquals(10, r(20, 10).getLower());
        assertEquals(20, r(20, 10).getFrom());
        assertEquals(10, r(20, 10).getTo());

        assertEquals(20, r(10, 20).getUpper());
        assertEquals(10, r(10, 20).getLower());
        assertEquals(10, r(10, 20).getFrom());
        assertEquals(20, r(10, 20).getTo());
    }

    @Test
    public void testContains() throws Exception {
        assertFalse(r(20, 10).contains(20));
        assertTrue(r(20, 10).contains(19));

        assertFalse(r(20, 10).contains(r(21, 10)));
        assertTrue(r(20, 10).contains(r(20, 10)));
        assertTrue(r(20, 10).contains(r(19, 11)));
        assertFalse(r(20, 10).contains(r(21, 9)));
    }

    @Test
    public void testIntersection() throws Exception {
        assertEquals(r(10, 20).intersection(r(14, 40)), r(14, 20));
        assertEquals(r(10, 20).intersection(r(21, 40)), null);
    }

    @Test
    public void testMerge() throws Exception {
        assertEquals(r(10, 20).tryMerge(r(14, 40)), r(10, 40));
        assertEquals(r(10, 20).tryMerge(r(21, 40)), null);
    }

    @Test
    public void testIntersects() throws Exception {
        assertTrue(r(10, 20).intersectsWith(r(20, 10)));
        assertTrue(r(10, 20).intersectsWith(r(19, 11)));
        assertTrue(r(19, 11).intersectsWith(r(20, 10)));
    }

    @Test
    public void testCheckConvert1() throws Exception {
        checkConvert(r(10, 20), 10);
        checkConvert(r(10, 20), 13);
        checkConvert(r(20, 10), 19);
        checkConvert(r(20, 10), 18);
    }

    private void checkConvert(Range range, int position) {
        int relativePosition = range.convertPointToRelativePosition(position);
        assertEquals(position, range.convertPointToAbsolutePosition(relativePosition));

        relativePosition = range.convertBoundaryToRelativePosition(position);
        assertEquals(position, range.convertBoundaryToAbsolutePosition(relativePosition));
    }
}
