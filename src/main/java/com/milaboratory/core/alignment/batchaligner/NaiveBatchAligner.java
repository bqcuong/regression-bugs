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
package com.milaboratory.core.alignment.batchaligner;

import com.milaboratory.core.alignment.Aligner;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.sequence.Sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NaiveBatchAligner<S extends Sequence<S>> {
    final NaiveBatchAlignerParameters<S> parameters;
    final List<S> references = new ArrayList<>();

    public NaiveBatchAligner(NaiveBatchAlignerParameters<S> parameters) {
        this.parameters = parameters;
    }

    public int addReference(S ref) {
        references.add(ref);
        return references.size() - 1;
    }

    public S get(int index) {
        return references.get(index);
    }

    public NaiveBatchAlignmentResult<S> align(final S sequence) {
        ArrayList<NaiveBatchAlignmentHit<S>> alignments = new ArrayList<>(references.size());
        for (int i = 0; i < references.size(); i++)
            alignments.add(alignSingle(i, sequence));
        Collections.sort(alignments, new HitComparator());
        float topScore = alignments.get(0).getAlignment().getScore();
        float scoreThreshold = Math.max(topScore * parameters.getRelativeMinScore(), parameters.getAbsoluteMinScore());
        for (int i = 0; i < alignments.size(); i++)
            if (i == parameters.getMaxHits() || alignments.get(i).getAlignment().getScore() < scoreThreshold)
                return new NaiveBatchAlignmentResult<>(new ArrayList<>(alignments.subList(0, i)));
        return new NaiveBatchAlignmentResult<>(alignments);
    }

    NaiveBatchAlignmentHit<S> alignSingle(int referenceId, S query) {
        Alignment<S> alignment = parameters.isGlobal() ?
                Aligner.alignGlobal(parameters.getScoring(), references.get(referenceId), query) :
                Aligner.alignLocal(parameters.getScoring(), references.get(referenceId), query);
        return new NaiveBatchAlignmentHit<>(referenceId, alignment);
    }

    private static class HitComparator implements Comparator<NaiveBatchAlignmentHit> {
        @Override
        public int compare(NaiveBatchAlignmentHit o1, NaiveBatchAlignmentHit o2) {
            return Float.compare(o2.getAlignment().getScore(), o1.getAlignment().getScore());
        }
    }
}
