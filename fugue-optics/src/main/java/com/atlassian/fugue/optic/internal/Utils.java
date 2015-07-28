/* Copyright (c) 2015, Jean-Baptiste Giraudeau <jb@giraudeau.info>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package com.atlassian.fugue.optic.internal;

import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Pair;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Utils {

  private Utils() {
  }

  /**
   * Performs function application within a higher-order function (applicative functor pattern).
   *
   * @param ca  A function to apply within a higher-order function.
   * @param cab The higher-order function to apply a function to.
   * @return A new function after applying the given higher-order function to the given function.
   */
  public static <A, B, C> Function<C, B> ap(final Function<C, A> ca, final Function<C, Function<A, B>> cab) {
    return m -> ca.andThen(cab.apply(m)).apply(m);
  }

  /**
   * Performs function application within a supplier (applicative functor pattern).
   *
   * @param sa supplier
   * @param sf The Supplier function to apply.
   * @return A new Supplier after applying the given Supplier function to the first argument.
   */
  public static <A, B> Supplier<B> ap(final Supplier<A> sa, final Supplier<Function<A, B>> sf) {
    return () -> sf.get().apply(sa.get());
  }

  /**
   * Performs function application within a stream (applicative functor pattern).
   *
   * @param as a stream
   * @param fs The stream of functions to apply.
   * @return A new stream after applying the given stream of functions through as.
   */
  public static <A, B> Stream<B> ap(final Stream<A> as, final Stream<Function<A, B>> fs) {
    final StreamMemoizer<A> memoizer = StreamMemoizer.memoizer(as);
    return fs.flatMap(f -> memoizer.stream().map(f));
  }

  /**
   * Performs function application within an iterable (applicative functor pattern).
   *
   * @param as an iterable
   * @param fs The iterable of functions to apply.
   * @return A new iterable after applying the given stream of functions through as.
   */
  public static <A, B> Iterable<B> ap(final Iterable<A> as, final Iterable<Function<A, B>> fs) {
    return Iterables.flatMap(fs, f -> Iterables.transform(as, f));
  }

  /**
   * Performs function application within an homogeneous pair (applicative functor pattern).
   *
   * @param aa an homogeneous pair
   * @param ff The pair of functions to apply.
   * @return A new pair after applying the given pair of functions through aa.
   */
  public static <A, B> Pair<B, B> ap(final Pair<A, A> aa, final Pair<Function<A, B>, Function<A, B>> ff) {
    return Pair.pair(ff.left().apply(aa.left()), ff.right().apply(aa.right()));
  }

  public static <A, B> Pair<B, B> map(final Pair<A, A> aa, final Function<A, B> f) {
    return Pair.pair(f.apply(aa.left()), f.apply(aa.right()));
  }
}
