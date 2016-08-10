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
package com.milaboratory.core.sequence.provider;

import com.milaboratory.core.Range;
import com.milaboratory.core.sequence.Sequence;

public final class SequenceProviderUtils {
    private SequenceProviderUtils() {
    }

    public static <S extends Sequence<S>> SequenceProvider<S> fromSequence(final S sequence) {
        return new SequenceProvider<S>() {
            @Override
            public S getRegion(Range range) {
                if (range.getUpper() > sequence.size())
                    throw new SequenceProviderIndexOutOfBoundsException(range.intersection(new Range(0, sequence.size())));
                return sequence.getRange(range);
            }
        };
    }

    public static <S extends Sequence<S>> SequenceProvider<S> lazyProvider(final SequenceProviderFactory<S> factory) {
        return new SequenceProvider<S>() {
            volatile SequenceProvider<S> innerProvider = null;

            @Override
            public S getRegion(Range range) {
                if (innerProvider == null)
                    synchronized (this) {
                        if (innerProvider == null)
                            innerProvider = factory.create();
                    }
                return innerProvider.getRegion(range);
            }
        };
    }
}
