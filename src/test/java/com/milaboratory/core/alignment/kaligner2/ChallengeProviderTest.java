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
package com.milaboratory.core.alignment.kaligner2;

import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.alignment.AffineGapAlignmentScoring;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.alignment.benchmark.Challenge;
import com.milaboratory.core.alignment.benchmark.ChallengeParameters;
import com.milaboratory.core.alignment.benchmark.ChallengeProvider;
import com.milaboratory.core.alignment.benchmark.KAlignerQuery;
import com.milaboratory.core.mutations.generator.GenericNucleotideMutationModel;
import com.milaboratory.core.mutations.generator.SubstitutionModels;
import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Test;

/**
 * Created by dbolotin on 11/11/15.
 */
public class ChallengeProviderTest {
    @Test
    public void test1() throws Exception {
        AffineGapAlignmentScoring<NucleotideSequence> scoring = new AffineGapAlignmentScoring<>(NucleotideSequence.ALPHABET, 10, -7, -9, -1);
        int absoluteMinScore = 70;

        ChallengeParameters params = new ChallengeParameters(100, 100, 500,
                100, 100,
                2, 2, 15, 50, 3, 30,
                0.45, 0.45, 0.9,
                new GenericNucleotideMutationModel(
                        SubstitutionModels.getEmpiricalNucleotideSubstitutionModel(),
                        0.000522, 0.000198).multiplyProbabilities(40),
                70, 150,
                scoring
        );
        ChallengeProvider.MAX_RERUNS = 10000;
        Challenge challenge = new ChallengeProvider(params, 11).take();
        OutputPort<KAlignerQuery> queries = challenge.queries();
        for (int i = 0; i < 100; i++) {
            Alignment<NucleotideSequence> ea = queries.take().expectedAlignment;
            System.out.println(ea.getScore());
            System.out.println(ea.getAlignmentHelper());
        }
    }
}