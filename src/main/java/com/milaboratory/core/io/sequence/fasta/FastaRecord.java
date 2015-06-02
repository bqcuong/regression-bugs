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
package com.milaboratory.core.io.sequence.fasta;

import com.milaboratory.core.sequence.Sequence;

public final class FastaRecord<S extends Sequence<S>> {
    private final long id;
    private final String description;
    private final S sequence;

    public FastaRecord(long id, String description, S sequence) {
        this.id = id;
        this.description = description;
        this.sequence = sequence;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public S getSequence() {
        return sequence;
    }
}
