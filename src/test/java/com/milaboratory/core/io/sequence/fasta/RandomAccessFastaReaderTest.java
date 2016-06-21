/*
 * Copyright 2016 MiLaboratory.com
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
package com.milaboratory.core.io.sequence.fasta;

import cc.redberry.pipe.CUtils;
import com.milaboratory.core.Range;
import com.milaboratory.core.io.sequence.fastq.SingleFastqReaderTest;
import com.milaboratory.core.sequence.AminoAcidSequence;
import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomAccessFastaReaderTest {
    @Test
    public void test1() throws Exception {
        Path path = new File(SingleFastqReaderTest.class.getClassLoader().getResource("sequences/some_fasta.fasta").toURI()).toPath();
        List<FastaRecord<AminoAcidSequence>> seqs = new ArrayList<>();

        try (FastaReader<AminoAcidSequence> r = new FastaReader<>(path.toFile(), AminoAcidSequence.ALPHABET)) {
            for (FastaRecord<AminoAcidSequence> rec : CUtils.it(r))
                seqs.add(rec);
        }

        try (RandomAccessFastaReader<AminoAcidSequence> raReader = new RandomAccessFastaReader<>(path, AminoAcidSequence.ALPHABET)) {
            ThreadLocalRandom r = ThreadLocalRandom.current();

            for (int i = 0; i < 1000; i++) {
                FastaRecord<AminoAcidSequence> rec = seqs.get(r.nextInt(seqs.size()));
                int from = r.nextInt(rec.getSequence().size() - 1);
                int to = r.nextInt(from, rec.getSequence().size());
                Range range = new Range(from, to);
                Assert.assertEquals(rec.getSequence().getRange(range), raReader.getSequence((int) rec.getId(), range));
                Assert.assertEquals(rec.getSequence().getRange(range), raReader.getSequence(rec.getDescription(), range));
            }
        }
    }

    @Test
    public void test2() throws Exception {
        try (RandomAccessFastaReader<NucleotideSequence> raReader =
                     new RandomAccessFastaReader<>("/Volumes/Data/Projects/MiLaboratory/tmp/NC_000007.14.fa",
                             NucleotideSequence.ALPHABET, true)) {
            System.out.println(raReader.getSequence("NC_000007.14", new Range(142560484, 142560931)));
            //for (int i = 0; i < raReader.getIndex().size(); i++) {
            //    System.out.println(raReader.getIndex().getRecordByIndex(i).getDescription());
            //    System.out.println(raReader.getIndex().getRecordByIndex(i).getLength());
            //}
        }
    }
}