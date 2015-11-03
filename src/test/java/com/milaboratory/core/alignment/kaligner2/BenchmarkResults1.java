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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BenchmarkResults1 {
    @JsonIgnore
    public final BenchmarkInput1 input;
    public final long executionTime;
    public final int processedQueries;
    public final int mismatched;
    public final int noHits;

    public BenchmarkResults1(BenchmarkInput1 input,
                             long executionTime, int processedQueries, int mismatched, int noHits) {
        this.input = input;
        this.executionTime = executionTime;
        this.processedQueries = processedQueries;
        this.mismatched = mismatched;
        this.noHits = noHits;
    }

    public BenchmarkInput1 getInput() {
        return input;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getProcessedQueries() {
        return processedQueries;
    }

    public int getMismatched() {
        return mismatched;
    }

    public int getNoHits() {
        return noHits;
    }

    public double getNoHitsFraction() {
        return 1.0 * noHits / processedQueries;
    }

    public double getMismatchedFraction() {
        return 1.0 * mismatched / processedQueries;
    }

    public double getBadFraction() {
        return 1.0 * (noHits + mismatched) / processedQueries;
    }

    public long getAverageTiming() {
        return executionTime / processedQueries;
    }
}
