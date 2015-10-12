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

package io.atlassian.fugue;

import static io.atlassian.fugue.Iterables.concat;
import static io.atlassian.fugue.Pair.pair;
import static java.util.Collections.singletonList;

/**
 * A Monoid is an algebraic structure consisting of an associative binary
 * operation across the values of a given type (a monoid is a {@link Semigroup})
 * and an identity element for this operation. Implementations must follow the
 * monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. append(zero(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. append(x, zero()) == x</li>
 * <li><em>Associativity</em>; forall x y z. append(append(x, y), z) ==
 * append(x, append(y, z))</li>
 * </ul>
 * Methods {@link #sum(Iterable)} and {@link #multiply(int, Object)} can be
 * overriden for performance reason, especially if {@link #sum(Iterable)} can be
 * implemented to not require evaluation of the whole iterable. All other
 * default methods should not be overriden.
 *
 * @see Semigroup
 * @since 3.1
 */
public interface Monoid<A> extends Semigroup<A> {

  /**
   * The identity element value for this monoid.
   *
   * @return The identity element for this monoid.
   */
  A zero();

  /**
   * Sums the given values.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  default A sum(final Iterable<A> as) {
    return Semigroup.super.sumNonEmpty(zero(), as);
  }

  /**
   * Returns a value summed <code>n</code> times (<code>a + a + ... + a</code>).
   * The default definition uses peasant multiplication, exploiting
   * associativity to only require `O(log n)` uses of
   * {@link #append(Object, Object)}.
   *
   * @param n multiplier
   * @param a the value to be reapeatly summed
   * @return <code>a</code> summed <code>n</code> times. If <code>n <= 0</code>,
   * returns <code>zero()</code>
   */
  default A multiply(final int n, final A a) {
    return (n <= 0) ? zero() : Semigroup.super.multiply1p(n - 1, a);
  }

  // Derived methods: should not be overriden:

  /**
   * Intersperses the given value between each two elements of the collection,
   * and sums the result.
   *
   * @param as An iterable of values.
   * @param a The value to intersperse between values of the given iterable.
   * @return The sum of the given values and the interspersed value.
   */
  default A intersperse(final Iterable<? extends A> as, final A a) {
    return sum(Iterables.intersperse(as, a));
  }

  @Override default A sumNonEmpty(A head, Iterable<A> tail) {
    return sum(concat(singletonList(head), tail));
  }

  @Override default A multiply1p(int n, A a) {
    return n == Integer.MAX_VALUE ? append(a, multiply(n, a)) : multiply(n + 1, a);
  }

  /**
   * Composes a monoid with another.
   */
  public static <A, B> Monoid<Pair<A, B>> compose(Monoid<A> ma, Monoid<B> mb) {
    Pair<A, B> zero = pair(ma.zero(), mb.zero());
    return new Monoid<Pair<A, B>>() {
      @Override public Pair<A, B> append(Pair<A, B> p1, Pair<A, B> p2) {
        return pair(ma.append(p1.left(), p2.left()), mb.append(p1.right(), p2.right()));
      }

      @Override public Pair<A, B> zero() {
        return zero;
      }
    };
  }

  /**
   * Return the dual Monoid.
   *
   * @param monoid a monoid.
   * @return a Monoid appending in reverse order,
   */
  public static <A> Monoid<A> dual(Monoid<A> monoid) {
    return new Monoid<A>() {
      @Override public A append(A a1, A a2) {
        return monoid.append(a2, a1);
      }

      @Override public A zero() {
        return monoid.zero();
      }

      @Override public A multiply(int n, A a) {
        return monoid.multiply(n, a);
      }
    };
  }

}