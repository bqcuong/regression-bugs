package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.test.TestUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by poslavsky on 15/09/15.
 */
public class KMapper2Test {
    public static final KAlignerParameters2 gParams = new KAlignerParameters2(5, false, false, 15, -20, 0.87f, 10, -7,
            -8, 4, 10, 4, 3, 0, 0, 0, 0, null);
//
//    @Test
//    public void testBestOffset1() throws Exception {
//        assertEquals(4, getBestOffset(new IntArrayList(-1, 2, 3, 3, 4, 4, 4), 10, 0, 0));
//        assertEquals(3, getBestOffset(new IntArrayList(-1, 2, 3, 3, 3, 4, 5), 10, 0, 0));
//        assertEquals(-1, getBestOffset(new IntArrayList(-1, -1, -1, 3, 3, 4, 5), 10, 0, 0));
//        assertEquals(3, getBestOffset(new IntArrayList(-1, -1, 3, 3, 3, 4, 5), 10, 0, 0));
//        assertEquals(-1, getBestOffset(new IntArrayList(-1, -1, 3, 3, 3, 4, 5), 3, 0, 0));
//        assertEquals(3, getBestOffset(new IntArrayList(-1, -1, -1, 3, 3, 4, 5), -1, 0, 0));
//        //assertEquals(23, getBestOffset(new IntArrayList(22, 23, 27, 54, 68, 93), Integer.MIN_VALUE, 0, 1));
//    }
//
//    @Test
//    public void testBestOffset2() {
//        //int v = -3358711;
//        int offset = -205;
//        int v = offset << 14 | 0;
//        IntArrayList list = new IntArrayList(1);
//        list.add(v);
//        //list.add((offset+1) << 14 | 0);
//        assertEquals(-205, getBestOffset(list, Integer.MIN_VALUE, 14, 3));
//        System.out.println(v >> 14);
//
//    }

    @Test
    public void test1() throws Exception {
        KMapper2 aligner = KMapper2.createFromParameters(gParams);
        aligner.addReference(new NucleotideSequence("ATTAGACACAATATATCTATGATCCTCTATTAGCTACGTACGGCTGATGCTAGTGTCGAT"));
        aligner.addReference(new NucleotideSequence("ACTAGCTGAGCTGTGTAGCTAGTATCTCGATATGCTACATCGTGGGTCGATTAGCTACGT"));
        aligner.addReference(new NucleotideSequence("GCTGTCGGCCTAGGCGCGATCGAACGCGCTGCGCGATGATATATCGCGATAATTCTCTGA"));

        for (int i = 0; i < TestUtil.its(1000, 50000); ++i) {            //GAACGCGCTGCGCGATGATATATCGCGATAATTCTCTGA
            KMappingResult2 result = aligner.align(new NucleotideSequence("GAACGCGCTGCGCGATGATATATCGCGATAATTCTCTGAAGTAGATGATGATGCAGCGTATG"));

            printResult(result);

            List<KMappingHit2> hits = result.hits;

            Assert.assertEquals("On i = " + i, 1, hits.size());
            Assert.assertEquals(-21, KMapper2.offset(hits.get(0).seedRecords[0]));
            Assert.assertEquals(2, hits.get(0).id);
        }
    }

    public static void printResult(KMappingResult2 result) {
        int i = 0;
        for (KMappingHit2 hit : result.getHits()) {
            System.out.println("Hit " + (i++) + ":");
            printHit(result, hit);
        }
    }

    public static void printHit(KMappingResult2 result, KMappingHit2 hit) {
        System.out.println("  ID: " + hit.id);
        System.out.println("  Island 0:");
        int boundaryI = 0;
        int[] seedRecords = hit.seedRecords;
        int i = 0;
        for (int seedRecord : seedRecords) {
            if (boundaryI < hit.boundaries.length && hit.boundaries[boundaryI] == i) {
                boundaryI++;
                System.out.println("  Island " + boundaryI + ":");
            }
            int index = KMapper2.index(seedRecord);
            int offset = KMapper2.offset(seedRecord);
            System.out.println("    " + result.getSeedPosition(index) + " : " + (result.getSeedPosition(index) - offset) + "  -  " + offset);
            i++;
        }
    }
//
//    @Test
//    public void test2() throws Exception {
//        com.milaboratory.core.alignment.KMapper aligner = com.milaboratory.core.alignment.KMapper.createFromParameters(gParams);
//        aligner.addReference(new NucleotideSequence("ATTAGACACAATATATCTATGATCCTCTATTAGCTACGTACGGCTGATGCTAGTGTCGAT"));
//        aligner.addReference(new NucleotideSequence("ACTAGCTGAGCTGTGTAGCTAGTATCTCGATATGCTACATCGTGGGTCGATTAGCTACGT"));
//        aligner.addReference(new NucleotideSequence("GCTGTCGGCCTAGGCGCGATCGAACGCGCTGCGCGATGATATATCGCGATAATTCTCTGA"));
//
//        KMappingResult2 result =
//                aligner.align(new NucleotideSequence("GACATTATATACAGACATATAATAAATACGGATACGCTGTCGGCCTAGGCGCGTCGAACGCGC"));
//
//        Assert.assertEquals(1, result.hits.size());
//        Assert.assertEquals(2, result.hits.get(0).id);
//    }

//    @Test
//    public void testRandom1() throws Exception {
//        RandomDataGenerator rdi = new RandomDataGenerator(new Well19937c(127368647891L));
//        int baseSize = its(500, 2000);
//        List<NucleotideSequence> ncs = new ArrayList<>(baseSize);
//        for (int i = 0; i < baseSize; ++i)
//            ncs.add(randomSequence(NucleotideSequence.ALPHABET, rdi, 40, 60));
//
//        com.milaboratory.core.alignment.KMapper ka = com.milaboratory.core.alignment.KMapper.createFromParameters(gParams.clone().setMapperMaxSeedsDistance(2).setMapperMinSeedsDistance(1).setMapperKValue(6));
//        for (NucleotideSequence seq : ncs)
//            ka.addReference(seq);
//
//        NucleotideMutationModel model = MutationModels.getEmpiricalNucleotideMutationModel().multiplyProbabilities(3.0);
//        int total = its(1000, 100000);
//        int found = 0;
//        OUTER:
//        for (int i = 0; i < total; ++i) {
//            int id = rdi.nextInt(0, baseSize - 1);
//            NucleotideSequence seq = ncs.get(id);
//            int subSize = rdi.nextInt(15, seq.size());
//            boolean left = (rdi.nextInt(0, 1) == 0);
//            NucleotideSequence target;
//            if (left)
//                target = seq.getRange(seq.size() - subSize, seq.size()).concatenate(randomSequence(NucleotideSequence.ALPHABET, rdi, 20, 30));
//            else
//                target = randomSequence(NucleotideSequence.ALPHABET, rdi, 20, 30).concatenate(seq.getRange(0, subSize));
//
//            int[] muts = generateMutations(target, model).getAllMutations();
//            target = mutate(target, muts);
//
//            KMappingResult2 result = ka.align(target);
//            List<KMappingHit2> hits = result.hits;
//            for (KMappingHit2 hit : hits) {
//
//                int previousSeedHit = -1, seedHit, seedHitOffset;
//                for (int k = 0; k < hit.seedOffsets.length; ++k) {
//                    seedHitOffset = hit.seedOffsets[k];
//                    if (seedHitOffset != KMapper2.SEED_NOT_FOUND_OFFSET)
//                        if (previousSeedHit == -1)
//                            previousSeedHit = result.seeds.get(hit.from + k) - seedHitOffset;
//                        else {
//                            seedHit = result.seeds.get(hit.from + k) - seedHitOffset;
//                            assertTrue(previousSeedHit < seedHit);
//                            previousSeedHit = seedHit;
//                        }
//                }
//
//                if (hit.id == id) {
//                    //Test for kmers
//                    for (int k = 0; k < hit.seedOffsets.length; ++k) {
//                        if (hit.seedOffsets[k] == SEED_NOT_FOUND_OFFSET)
//                            continue;
//                        int kmer1 = 0, kmer2 = 0;
//                        //get kmer in target sequence
//                        int targetFrom = result.seeds.get(hit.from + k);
//                        for (int j = targetFrom; j < targetFrom + ka.getKValue(); ++j)
//                            kmer1 = kmer1 << 2 | target.codeAt(j);
//
//                        //kmer in ref sequence
//                        int refFrom = targetFrom - hit.seedOffsets[k];
//                        for (int j = refFrom; j < refFrom + ka.getKValue(); ++j)
//                            kmer2 = kmer2 << 2 | ncs.get(hit.id).codeAt(j);
//
//                        Assert.assertEquals(kmer1, kmer2);
//                    }
//                    ++found;
//                    continue OUTER;
//                }
//            }
//
//        }
//
//        Assert.assertTrue((float) found / total > 0.85f);
//    }
}