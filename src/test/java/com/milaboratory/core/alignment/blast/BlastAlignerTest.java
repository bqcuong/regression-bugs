package com.milaboratory.core.alignment.blast;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.alignment.batch.AlignmentHit;
import com.milaboratory.core.alignment.batch.PipedAlignmentResult;
import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BlastAlignerTest {
    @Test
    public void test1() throws Exception {
        List<NucleotideSequence> seqs = new ArrayList<>();
        seqs.add(new NucleotideSequence("ATTAGACACAGACACA"));
        seqs.add(new NucleotideSequence("GATACACCACCGATGCTGGAGATGCATGCTAGCGGCGCGGATAGCTGCATG"));
        long bases = 0;
        for (NucleotideSequence seq : seqs)
            bases += seq.size();

        BlastDB db = BlastDBBuilder.build(seqs);

        BlastAligner<NucleotideSequence> aligner = new BlastAligner<>(db);
        OutputPort<PipedAlignmentResult<AlignmentHit<NucleotideSequence, BlastHitInfo>, NucleotideSequence>> results = aligner.align(CUtils.asOutputPort(seqs));
        for (PipedAlignmentResult<AlignmentHit<NucleotideSequence, BlastHitInfo>, NucleotideSequence> result : CUtils.it(results)) {
            System.out.println(result);
        }
    }
}