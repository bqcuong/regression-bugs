package com.milaboratory.core.alignment.blast;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.alignment.batch.PipedAlignmentResult;
import com.milaboratory.core.sequence.AminoAcidSequence;
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
        OutputPort<PipedAlignmentResult<ExternalDBBlastHit<NucleotideSequence>, NucleotideSequence>> results = aligner.align(CUtils.asOutputPort(seqs));
        for (PipedAlignmentResult<ExternalDBBlastHit<NucleotideSequence>, NucleotideSequence> result : CUtils.it(results)) {
            System.out.println(result);
        }
    }

    @Test
    public void test2() throws Exception {
        List<AminoAcidSequence> seqs = new ArrayList<>();
        seqs.add(new AminoAcidSequence("PMISVGGVKCYMVRLTNFLQVFIRITISSYHLDMVKQVWLFYVEVIRLWFIVLDSTGSV"));
        seqs.add(new AminoAcidSequence("LNGMSYNNKDLLNIKNTINNYEVMPNLKIPYDKMNDYWI"));

        BlastDB db = BlastDB.get("/Volumes/Data/tools/ncbi-blast-2.2.31+/db/yeast");
        BlastAligner<AminoAcidSequence> aligner = new BlastAligner<>(db);
        OutputPort<PipedAlignmentResult<ExternalDBBlastHit<AminoAcidSequence>, AminoAcidSequence>> results = aligner.align(CUtils.asOutputPort(seqs));
        for (PipedAlignmentResult<ExternalDBBlastHit<AminoAcidSequence>, AminoAcidSequence> result : CUtils.it(results)) {
            System.out.println(result);
            for (ExternalDBBlastHit<AminoAcidSequence> hit : result.getHits()) {
                System.out.println(hit.getAlignment().getAlignmentHelper());
            }
        }
    }
}