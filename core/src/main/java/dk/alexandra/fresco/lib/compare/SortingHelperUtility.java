/*
 * Copyright (c) 2016 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL,
 * and Bouncy Castle. Please see these projects for any further licensing issues.
 */
package dk.alexandra.fresco.lib.compare;


import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.numeric.Comparison;
import dk.alexandra.fresco.framework.builder.numeric.Numeric;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.value.SInt;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SortingHelperUtility {

  public DRes<SInt> isSorted(ProtocolBuilderNumeric builder,
      List<DRes<SInt>> values) {
    return builder.par(par -> {
      Comparison comparison = par.comparison();
      ArrayList<DRes<SInt>> comparisons = new ArrayList<>();
      boolean first = true;

      DRes<SInt> previous = null;
      for (DRes<SInt> value : values) {
        if (!first) {
          comparisons.add(comparison.compareLEQ(previous, value));
        } else {
          first = false;
        }
        previous = value;
      }
      return () -> comparisons;
    }).seq((seq, comparison) -> seq.advancedNumeric().product(comparison));
  }


  private static int FloorLog2(int value) {
    int result = -1;
    for (int i = 1; i < value; i <<= 1, ++result) {
      ;
    }
    return result;
  }

  final BigInteger minusOne = BigInteger.valueOf(-1L);

  public void compareAndSwap(ProtocolBuilderNumeric builder, int a, int b,
      List<DRes<SInt>> values) {
    //Non splitting version

    Numeric numeric = builder.numeric();

    DRes<SInt> valueA = values.get(a);
    DRes<SInt> valueB = values.get(b);
    DRes<SInt> comparison = builder.comparison().compareLEQ(valueA, valueB);
    DRes<SInt> sub = numeric.sub(valueA, valueB);
    DRes<SInt> c = numeric.mult(comparison, sub);
    DRes<SInt> d = numeric.mult(minusOne, c);

    //a = comparison*a+(1-comparison)*b ==> comparison*(a-b)+b
    //b = comparison*b+(1-comparison)*a ==>  -comparison*(a-b)+a
    builder.par(par -> {
      values.set(a, par.numeric().add(c, valueB));
      values.set(b, par.numeric().add(d, valueA));
      return null;
    });
  }


  public void sort(ProtocolBuilderNumeric builder, List<DRes<SInt>> values) {
    //sort using Batcher´s Merge Exchange

    int t = FloorLog2(values.size());
    int p0 = (1 << t);

    builder
        .seq(seq -> () -> p0)
        .whileLoop(
            p -> p > 0,
            (seq, p) -> {
              seq.seq(innerSeq -> () -> new Iteration(p0, 0, p)
              ).whileLoop(
                  state -> state.r == 0 || state.q != p,
                  (whileSeq, state) -> {
                    final int d = state.r == 0 ? state.d : state.q - p;
                    final int q = state.r == 0 ? state.q : state.q / 2;

                    return whileSeq.par(par -> {
                      for (int i = 0; i < values.size() - d; i++) {
                        if ((i & p) == state.r) {
                          int finalI = i;
                          par.createIteration(seq3 ->
                              compareAndSwap(seq3, finalI, finalI + d, values)
                          );
                        }
                      }
                      return () -> new Iteration(q, p, d);
                    });
                  }
              );
              return () -> p / 2;
            });
  }

  private static class Iteration {

    final int q;
    final int r;
    final int d;

    private Iteration(int q, int r, int d) {
      this.q = q;
      this.r = r;
      this.d = d;
    }
  }
}