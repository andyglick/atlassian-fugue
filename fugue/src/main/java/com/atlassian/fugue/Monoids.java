/*
   Copyright 2015 Atlassian

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.atlassian.fugue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.Iterables.map;
import static com.atlassian.fugue.Monoid.monoid;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Unit.Unit;
import static java.util.Collections.emptyList;

/**
 * {@link Monoid} instances.
 *
 * @since 3.0
 */
public final class Monoids {

  /**
   * A monoid that adds integers.
   */
  public static final Monoid<Integer> intAddition = monoid(Semigroups.intAddition, 0, (n, i) -> n * i);

  /**
   * A monoid that multiplies integers.
   */
  public static final Monoid<Integer> intMultiplication = monoid(Semigroups.intMultiplication, 1);

  /**
   * A monoid that adds doubles.
   */
  public static final Monoid<Double> doubleAddition = monoid(Semigroups.doubleAddition, 0.0, (n, d) -> d * n);

  /**
   * A monoid that adds big integers.
   */
  public static final Monoid<BigInteger> bigintAddition = monoid(Semigroups.bigintAddition, BigInteger.ZERO,
    (n, b) -> b.multiply(BigInteger.valueOf(n)));

  /**
   * A monoid that multiplies big integers.
   */
  public static final Monoid<BigInteger> bigintMultiplication = monoid(Semigroups.bigintMultiplication, BigInteger.ONE, (n, b) -> b.pow(n));

  /**
   * A monoid that adds longs.
   */
  public static final Monoid<Long> longAddition = monoid(Semigroups.longAddition, 0L, (n, l) -> l * n);

  /**
   * A monoid that multiplies longs.
   */
  public static final Monoid<Long> longMultiplication = monoid(Semigroups.longMultiplication, 1L);

  /**
   * A monoid that ORs booleans.
   */
  public static final Monoid<Boolean> disjunction = monoid(Semigroups.disjunction, false, bs -> Iterables.filter(bs, b -> b).iterator().hasNext(),
    (n, b) -> b);

  /**
   * A monoid that XORs booleans.
   */
  public static final Monoid<Boolean> exclusiveDisjunction = monoid(Semigroups.exclusiveDisjunction, false, (n, b) -> b && n == 1);

  /**
   * A monoid that ANDs booleans.
   */
  public static final Monoid<Boolean> conjunction = monoid(Semigroups.conjunction, true, bs -> !Iterables.filter(bs, b -> !b).iterator().hasNext());

  /**
   * A monoid that appends strings.
   */
  public static final Monoid<String> string = monoid(Semigroups.string, "", ss -> {
    StringBuilder sb = new StringBuilder();
    for (String s : ss) {
      sb.append(s);
    }
    return sb.toString();
  }, (n, s) -> {
    StringBuilder sb = new StringBuilder(n * s.length());
    for (int i = 0; i < n; i++) {
      sb.append(s);
    }
    return sb.toString();
  });

  /**
   * A monoid for the Unit value.
   */
  public static final Monoid<Unit> unit = monoid(Semigroups.unit, Unit(), us -> Unit(), (n, u) -> Unit());

  private Monoids() {
  }

  /**
   * A monoid for functions.
   *
   * @param mb The monoid for the function codomain.
   * @return A monoid for functions.
   */
  public static <A, B> Monoid<Function<A, B>> function(final Monoid<B> mb) {
    return monoid(Semigroups.function(mb), f -> mb.zero(), fs -> a -> mb.sum(map(fs, Functions.<A, B>apply(a))),
      (n, f) -> a -> mb.multiply(n, f.apply(a)));
  }

  /**
   * A monoid for lists.
   *
   * @return A monoid for lists.
   */
  public static <A> Monoid<List<A>> list() {
    return monoid(Semigroups.<A>list(), emptyList(), ls -> {
      final List<A> r = new ArrayList<>();
      for (final List<A> l : ls) {
        r.addAll(l);
      }
      return r;
    });
  }

  /**
   * A monoid for iterables.
   *
   * @return A monoid for iterables.
   */
  public static <A> Monoid<Iterable<A>> iterable() {
    return monoid(Semigroups.<A>iterable(), emptyList(), Iterables::join);
  }

  /**
   * A monoid for options (that take the first available value).
   *
   * @return A monoid for options (that take the first available value).
   */
  public static <A> Monoid<Option<A>> firstOption() {
    return monoid(Semigroups.<A>firstOption(), none(), os -> Iterables.first(Options.filterNone(os)).getOrElse(none()));
  }

  /**
   * A monoid for options that take the last available value.
   *
   * @return A monoid for options that take the last available value.
   */
  public static <A> Monoid<Option<A>> lastOption() {
    return monoid(Semigroups.lastOption(), none());
  }

  /**
   * A monoid for options that combine inner value with a semigroup.
   *
   * @return A monoid for options that combine inner value with a semigroup.
   */
  public static <A> Monoid<Option<A>> option(Semigroup<A> semigroup) {
    return monoid((o1, o2) -> o1.fold(() -> o2, a1 -> o2.fold(() -> o1, a2 -> some(semigroup.append(a1, a2)))), none());
  }

  /**
   * A monoid Sums up values inside either {@see Semigroups#either}.
   * Monoid of right values provide the identity element of the resulting monoid.
   *
   * @param lS semigroup for left values
   * @param rM monoid for right values.
   * @return A monoid Sums up values inside either.
   */
  public static <L, R> Monoid<Either<L, R>> either(Semigroup<L> lS, Monoid<R> rM) {
    return monoid(Semigroups.either(lS, rM), right(rM.zero()));
  }

}
