/*
 * MiTCR <http://milaboratory.com>
 *
 * Copyright (c) 2010-2013:
 *     Bolotin Dmitriy     <bolotin.dmitriy@gmail.com>
 *     Chudakov Dmitriy    <chudakovdm@mail.ru>
 *
 * MiTCR is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.milaboratory.core.sequence;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class NucleotideSequencesTest {
    @Test
    public void test1() {
        NucleotideSequence sequence = new NucleotideSequence("ATTAGACATAGACA");
        assertEquals(sequence.toString(), "ATTAGACATAGACA");
        NucleotideSequence subSequence = sequence.getRange(0, sequence.size());
        assertEquals(subSequence.toString(), "ATTAGACATAGACA");
        assertEquals(subSequence.hashCode(), sequence.hashCode());
        assertEquals(subSequence, sequence);

        NucleotideSequence sequence1 = new NucleotideSequence("AGACATAGACA");
        NucleotideSequence subSequence1 = sequence.getRange(3, sequence.size());

        assertEquals(subSequence1.hashCode(), sequence1.hashCode());
        assertEquals(subSequence1, sequence1);
        assertEquals(NucleotideSequence.EMPTY, NucleotideSequence.EMPTY.getReverseComplement());
    }

    @Test
    public void testConcatenate1() throws Exception {
        NucleotideSequence s1 = new NucleotideSequence("ATTAGACA"),
                s2 = new NucleotideSequence("GACATATA");

        assertEquals(new NucleotideSequence("ATTAGACAGACATATA"), s1.concatenate(s2));
    }

    @Test
    public void testConcatenate2() throws Exception {
        NucleotideSequence s1 = new NucleotideSequence("ATTAGACA"),
                s2 = new NucleotideSequence("");

        assertEquals(new NucleotideSequence("ATTAGACA"), s1.concatenate(s2));
        assertEquals(new NucleotideSequence("ATTAGACA"), s2.concatenate(s1));
    }

    @Test
    public void testConcatenate3() throws Exception {
        NucleotideSequence s1 = new NucleotideSequence(""),
                s2 = new NucleotideSequence("");

        assertEquals(new NucleotideSequence(""), s1.concatenate(s2));
        assertEquals(new NucleotideSequence(""), s2.concatenate(s1));
    }

    @Test
    public void testRC1() {
        NucleotideSequence ns = new NucleotideSequence("atagagaattagataaggcagatacgatcgacgtgtactactagcta");
        NucleotideSequence rc = ns.getReverseComplement();
        NucleotideSequence rcrc = rc.getReverseComplement();
        assertEquals(rcrc, ns);
        assertEquals(rcrc.hashCode(), ns.hashCode());
        assertThat(rc, not(ns));
        assertThat(rc.hashCode(), not(ns.hashCode()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownSymbol1() throws Exception {
        new NucleotideSequence("ATTAN");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownSymbol2() throws Exception {
        new NucleotideSequence(new char[]{'a', 'n', 'k'});
    }
}
