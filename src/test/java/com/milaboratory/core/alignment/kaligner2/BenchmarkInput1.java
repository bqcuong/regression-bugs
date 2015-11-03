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

import com.milaboratory.core.alignment.KAlignerParameters;

/**
 * Created by dbolotin on 27/10/15.
 */
public final class BenchmarkInput1 {
    public final KAlignerParameters params;
    public final Challenge challenge;

    public BenchmarkInput1(KAlignerParameters params, Challenge challenge) {
        this.params = params;
        this.challenge = challenge;
    }
}
