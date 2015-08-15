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

import java.util.stream.Stream;

/**
 * A monoid abstraction to be defined across types of the given type argument. Implementations must follow the monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. append(empty(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. append(x, empty()) == x</li>
 * <li><em>Associativity</em>; forall  x y z. append(append(x, y), z) == append(x, append(y, z))</li>
 * </ul>
 */
public interface Monoid<A> extends Semigroup<A> {

  /**
   * The identity element value for this monoid.
   *
   * @return The identity element for this monoid.
   */
  A empty();

  @Override default Monoid<A> flipped() {
    return monoid(Semigroup.super.flipped(), empty());
  }

  /**
   * Sums the given values.
   *
   * @param as The values to append.
   * @return The append of the given values.
   */
  default A join(final Iterable<A> as) {
    A m = empty();
    for (A a : as) {
      m = append(m, a);
    }
    return m;
  }

  /**
   * Sums the given values.
   *
   * @param as The values to append.
   * @return The append of the given values.
   */
  default A join(final Stream<A> as) {
    return as.reduce(empty(), this);
  }

  /**
   * Returns a value summed <code>n</code> times (<code>a + a + ... + a</code>)
   *
   * @param n multiplier
   * @param a the value to joinRepeated
   * @return <code>a</code> summed <code>n</code> times. If <code>n <= 0</code>, returns <code>empty()</code>
   */
  default A joinRepeated(final int n, final A a) {
    A m = empty();
    for (int i = 0; i < n; i++) {
      m = append(m, a);
    }
    return m;
  }

  /**
   * Intersperses the given value between each two elements of the stream, and sums the result.
   *
   * @param as An stream of values to append.
   * @param a  The value to intersperse between values of the given iterable.
   * @return The append of the given values and the interspersed value.
   */
  default A joinInterspersedStream(final Stream<A> as, final A a) {
    return as.reduce((a1, a2) -> append(a1, append(a, a2))).orElse(empty());
  }

  /**
   * Intersperses the given value between each two elements of the collection, and sums the result.
   *
   * @param as An stream of values to append.
   * @param a  The value to intersperse between values of the given iterable.
   * @return The append of the given values and the interspersed value.
   */
  default A joinInterspersed(final Iterable<A> as, final A a) {
    return join(Iterables.intersperse(as, a));
  }

  /**
   * Composes this monoid with another.
   */
  default <B> Monoid<Pair<A, B>> composeMonoid(Monoid<B> mb) {
    return monoid(composeSemigroup(mb), Pair.pair(empty(), mb.empty()));
  }

  /**
   * Constructs a monoid from the given semigroup (append function) and empty value, which must follow the monoidal laws.
   *
   * @param semigroup The semigroup for the monoid.
   * @param zero      The empty for the monoid.
   * @return A monoid instance that uses the given semigroup and empty value.
   */
  static <A> Monoid<A> monoid(final Semigroup<A> semigroup, final A zero) {
    return new Monoid<A>() {

      @Override public A append(final A a1, final A a2) {
        return semigroup.append(a1, a2);
      }

      @Override public A empty() {
        return zero;
      }
    };
  }

}