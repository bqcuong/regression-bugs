package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.core.Range;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.mutations.generator.MutationModels;
import com.milaboratory.core.mutations.generator.MutationsGenerator;
import com.milaboratory.core.mutations.generator.NucleotideMutationModel;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.sequence.SequenceBuilder;
import com.milaboratory.test.TestUtil;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.Well1024a;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
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
    public void testRecord() throws Exception {
        RandomDataGenerator random = new RandomDataGenerator(new Well1024a());
        for (int i = 0; i < 10000; ++i) {
            int offset = random.nextInt(-1000, 1000);
            int index = random.nextInt(0, 1000);
            int record = KMapper2.record(offset, index);
            Assert.assertEquals(index, KMapper2.index(record));
            Assert.assertEquals(offset, KMapper2.offset(record));
        }
    }

    @Test
    public void test1() throws Exception {
        KMapper2 aligner = KMapper2.createFromParameters(gParams);
        aligner.addReference(new NucleotideSequence("ATTAGACACAATATATCTATGATCCTCTATTAGCTACGTACGGCTGATGCTAGTGTCGAT"));
        aligner.addReference(new NucleotideSequence("ACTAGCTGAGCTGTGTAGCTAGTATCTCGATATGCTACATCGTGGGTCGATTAGCTACGT"));
        aligner.addReference(new NucleotideSequence("GCTGTCGGCCTAGGCGCGATCGAACGCGCTGCGCGATGATATATCGCGATAATTCTCTGA"));

        for (int i = 0; i < TestUtil.its(1000, 50000); ++i) {            //GAACGCGCTGCGCGATGATATATCGCGATAATTCTCTGA
            KMappingResult2 result = aligner.align(new NucleotideSequence("GAACGCGCTGCGCGATGATATATCGCGATAATTCTCTGAAGTAGATGATGATGCAGCGTATG"));

            System.out.println(result);

            List<KMappingHit2> hits = result.hits;

            Assert.assertEquals("On i = " + i, 1, hits.size());
            Assert.assertEquals(-21, KMapper2.offset(hits.get(0).seedRecords[0]));
            Assert.assertEquals(2, hits.get(0).id);
        }
    }

    public static void assertGoodSequenceOfIndices(KMappingResult2 result2) {
        for (KMappingHit2 hit : result2.hits)
            assertGoodSequenceOfIndices(hit);
    }

    public static void assertGoodSequenceOfIndices(KMappingHit2 hit) {
        int[] seedRecords = hit.seedRecords;
        for (int i = 1; i < seedRecords.length; i++)
            if (KMapper2.index(seedRecords[i - 1]) > KMapper2.index(seedRecords[i]))
                throw new AssertionError("Wrong sequence of seeds:\n" + hit);
    }

    @Test
    public void testRandom1() throws Exception {
        ChallengeParameters cp = DEFAULT;
        RandomDataGenerator generator = new RandomDataGenerator(new Well1024a(1233));
        NucleotideSequence[] db = generateDB(generator, cp);
        KMapper2 kMapper = KMapper2.createFromParameters(gParams);
        for (NucleotideSequence ns : db)
            kMapper.addReference(ns);
        DescriptiveStatistics timing = new DescriptiveStatistics(),
                clusters = new DescriptiveStatistics();
        long noHits = 0, wrongHit = 0;
        for (int i = 0; i < 10_000; ++i) {
            Challenge challenge = createChallenge(cp, generator, db);
            long start = System.nanoTime();
            KMappingResult2 result = kMapper.align(challenge.query);
            timing.addValue(System.nanoTime() - start);

            assertGoodSequenceOfIndices(result);

            if (result.getHits().size() == 0) {
                ++noHits;
                continue;
            }

            KMappingHit2 top = result.getHits().get(0);
            if (top.id != challenge.targetId) {
                ++wrongHit;
                continue;
            }

            clusters.addValue(top.boundaries.length);
        }

        System.out.println("noHits: " + noHits);
        System.out.println("wrongHit: " + wrongHit);

        System.out.println("\n\n\n");
        System.out.println("Timings:");
        System.out.println(timing);

        System.out.println("\n\n\n");
        System.out.println("Clusters count");
        System.out.println(clusters);
    }

    public static NucleotideSequence[] generateDB(RandomDataGenerator generator, ChallengeParameters params) {
        NucleotideSequence[] db = new NucleotideSequence[params.dbSize];
        for (int i = 0; i < params.dbSize; i++)
            db[i] = TestUtil.randomSequence(NucleotideSequence.ALPHABET, generator, params.dbMinSeqLength, params.dbMaxSeqLength);
        return db;
    }

    public static final ChallengeParameters DEFAULT = new ChallengeParameters(100, 100, 500,
            1, 4, 15, 50, 3, 30,
            0.45, 0.45, 0.5,
            MutationModels.getEmpiricalNucleotideMutationModel().multiplyProbabilities(5));

    public static Challenge createChallenge(ChallengeParameters cp, RandomDataGenerator generator,
                                            NucleotideSequence[] db) {
        int targetId = generator.nextInt(0, db.length - 1);
        NucleotideSequence target = db[targetId];
        SequenceBuilder<NucleotideSequence> queryBuilder = NucleotideSequence.ALPHABET.getBuilder();
        if (generator.nextUniform(0, 1) < cp.boundaryInsertProbability)
            queryBuilder.append(TestUtil.randomSequence(NucleotideSequence.ALPHABET, generator, cp.minIndelLength, cp.maxIndelLength, true));

        List<Range> tRanges = new ArrayList<>(), qRanges = new ArrayList<>();
        List<Mutations<NucleotideSequence>> muts = new ArrayList<>();

        int tOffset = generator.nextInt(0, cp.maxIndelLength), qOffset = queryBuilder.size();
        Range r;
        Mutations<NucleotideSequence> m;
        NucleotideSequence ins;
        double v;
        for (int i = generator.nextInt(cp.minClusters, cp.maxClusters); i >= 0; --i)
            if (tRanges.isEmpty()) {
                r = new Range(tOffset, tOffset += generator.nextInt(cp.minClusterLength, cp.maxClusterLength));
                if (r.getTo() > target.size())
                    break;
                tRanges.add(r);
                muts.add(m = MutationsGenerator.generateMutations(target, cp.mutationModel, r));
                qRanges.add(new Range(qOffset, qOffset += r.length() + m.getLengthDelta()));
                queryBuilder.append(m.move(-r.getFrom()).mutate(target.getRange(r)));
            } else {
                if ((v = generator.nextUniform(0, 1.0)) < cp.insertionProbability)
                    ins = TestUtil.randomSequence(NucleotideSequence.ALPHABET, generator, cp.minIndelLength, cp.maxIndelLength, true);
                else if (v < cp.insertionProbability + cp.deletionProbability) {
                    tOffset += generator.nextInt(cp.minIndelLength, cp.maxIndelLength);
                    ins = NucleotideSequence.EMPTY;
                } else {
                    ins = TestUtil.randomSequence(NucleotideSequence.ALPHABET, generator, cp.minIndelLength, cp.maxIndelLength, true);
                    tOffset += generator.nextInt(cp.minIndelLength, cp.maxIndelLength);
                }
                r = new Range(tOffset, tOffset += generator.nextInt(cp.minClusterLength, cp.maxClusterLength));
                if (r.getTo() > target.size())
                    break;
                tRanges.add(r);
                muts.add(m = MutationsGenerator.generateMutations(target, cp.mutationModel, r));
                qRanges.add(new Range(qOffset += ins.size(), qOffset += r.length() + m.getLengthDelta()));
                queryBuilder.append(ins).append(m.move(-r.getFrom()).mutate(target.getRange(r)));
            }

        if (generator.nextUniform(0, 1) < cp.boundaryInsertProbability)
            queryBuilder.append(TestUtil.randomSequence(NucleotideSequence.ALPHABET, generator, cp.minIndelLength, cp.maxIndelLength, true));

        return new Challenge(targetId, qRanges, tRanges, muts, queryBuilder.createAndDestroy());
    }

    public static final class ChallengeParameters {
        final int dbSize, dbMinSeqLength, dbMaxSeqLength;
        final int minClusters, maxClusters,
                minClusterLength, maxClusterLength,
                minIndelLength, maxIndelLength;
        final double insertionProbability, deletionProbability, boundaryInsertProbability;
        final NucleotideMutationModel mutationModel;

        public ChallengeParameters(int dbSize, int dbMinSeqLength, int dbMaxSeqLength,
                                   int minClusters, int maxClusters, int minClusterLength,
                                   int maxClusterLength, int minIndelLength, int maxIndelLength,
                                   double insertionProbability, double deletionProbability, double boundaryInsertProbability,
                                   NucleotideMutationModel mutationModel) {
            this.dbSize = dbSize;
            this.dbMinSeqLength = dbMinSeqLength;
            this.dbMaxSeqLength = dbMaxSeqLength;
            this.minClusters = minClusters;
            this.maxClusters = maxClusters;
            this.minClusterLength = minClusterLength;
            this.maxClusterLength = maxClusterLength;
            this.minIndelLength = minIndelLength;
            this.maxIndelLength = maxIndelLength;
            this.insertionProbability = insertionProbability;
            this.deletionProbability = deletionProbability;
            this.boundaryInsertProbability = boundaryInsertProbability;
            this.mutationModel = mutationModel;
        }
    }

    public static final class Challenge {
        final int targetId;
        final List<Range> queryClusters, targetClusters;
        final List<Mutations<NucleotideSequence>> mutationsInTarget;
        final NucleotideSequence query;

        public Challenge(int targetId, List<Range> queryClusters, List<Range> targetClusters,
                         List<Mutations<NucleotideSequence>> mutationsInTarget, NucleotideSequence query) {
            this.targetId = targetId;
            this.queryClusters = queryClusters;
            this.targetClusters = targetClusters;
            this.mutationsInTarget = mutationsInTarget;
            this.query = query;
        }

        @Override
        public String toString() {
            return "Challenge{" +
                    "targetId=" + targetId +
                    ", queryClusters=" + queryClusters +
                    ", targetClusters=" + targetClusters +
                    ", mutationsInTarget=" + mutationsInTarget +
                    ", query=" + query +
                    '}';
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