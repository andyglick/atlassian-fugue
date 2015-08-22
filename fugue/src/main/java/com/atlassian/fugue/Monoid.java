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

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.atlassian.fugue.Pair.pair;

/**
 * A Monoid is an algebraic structure consisting of an associative binary operation across the values of a given type (a monoid is a {@link Semigroup})
 * and an identity element for this operation.
 * Implementations must follow the monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. append(zero(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. append(x, zero()) == x</li>
 * <li><em>Associativity</em>; forall  x y z. append(append(x, y), z) == append(x, append(y, z))</li>
 * </ul>
 *
 * @since 3.0
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
   * @param as The values to append.
   * @return The append of the given values.
   */
  default A sum(final Iterable<A> as) {
    return sumNel(zero(), as);
  }

  /**
   * Returns a value summed <code>n</code> times (<code>a + a + ... + a</code>)
   *
   * @param n multiplier
   * @param a the value to be reapeatly summed
   * @return <code>a</code> summed <code>n</code> times. If <code>n <= 0</code>, returns <code>zero()</code>
   */
  default A multiply(final int n, final A a) {
    return n <= 0 ? zero() : multiply1p(n - 1, a);
  }

  /**
   * Constructs a monoid from the given semigroup (append function) and zero value, which must follow the monoidal laws.
   *
   * @param semigroup The semigroup for the monoid.
   * @param zero      The zero for the monoid.
   * @return A monoid instance that uses the given semigroup and zero value.
   */
  static <A> Monoid<A> monoid(final Semigroup<A> semigroup, final A zero) {
    return new Monoid<A>() {

      @Override public A append(final A a1, final A a2) {
        return semigroup.append(a1, a2);
      }

      @Override public A zero() {
        return zero;
      }
    };
  }

  /**
   * Constructs a monoid from the given semigroup (append function) and zero value, which must follow the monoidal laws,
   * and provide an optimized implementation of sum.
   *
   * @param semigroup The semigroup for the monoid.
   * @param zero      The zero for the monoid.
   * @param sum       optimized sum implementation.
   * @return A monoid instance that uses the given semigroup and zero value.
   */
  static <A> Monoid<A> monoid(final Semigroup<A> semigroup, final A zero, Function<Iterable<A>, A> sum) {
    return new Monoid<A>() {

      @Override public A append(final A a1, final A a2) {
        return semigroup.append(a1, a2);
      }

      @Override public A zero() {
        return zero;
      }

      @Override public A sum(Iterable<A> as) {
        return sum.apply(as);
      }
    };
  }

  /**
   * Constructs a monoid from the given semigroup (append function) and zero value, which must follow the monoidal laws,
   * and provide an optimized implementation of multiply.
   *
   * @param semigroup The semigroup for the monoid.
   * @param zero      The zero for the monoid.
   * @param multiply  optimized multiply implementation.
   * @return A monoid instance that uses the given semigroup and zero value.
   */
  static <A> Monoid<A> monoid(final Semigroup<A> semigroup, final A zero, BiFunction<Integer, A, A> multiply) {
    return new Monoid<A>() {

      @Override public A append(final A a1, final A a2) {
        return semigroup.append(a1, a2);
      }

      @Override public A zero() {
        return zero;
      }

      @Override public A multiply(int n, A a) {
        return n <= 0 ? zero() : multiply.apply(n, a);
      }
    };
  }

  /**
   * Constructs a monoid from the given semigroup (append function) and zero value, which must follow the monoidal laws,
   * and provide optimized implementations of sum and multiply.
   *
   * @param semigroup The semigroup for the monoid.
   * @param zero      The zero for the monoid.
   * @param sum       optimized sum implementation.
   * @param multiply  optimized multiply implementation.
   * @return A monoid instance that uses the given semigroup and zero value.
   */
  static <A> Monoid<A> monoid(final Semigroup<A> semigroup, final A zero, Function<Iterable<A>, A> sum, BiFunction<Integer, A, A> multiply) {
    return new Monoid<A>() {

      @Override public A append(final A a1, final A a2) {
        return semigroup.append(a1, a2);
      }

      @Override public A zero() {
        return zero;
      }

      @Override public A sum(Iterable<A> as) {
        return sum.apply(as);
      }

      @Override public A multiply(int n, A a) {
        return n <= 0 ? zero() : multiply.apply(n, a);
      }
    };
  }

  /**
   * Composes a monoid with another.
   */
  static <A, B> Monoid<Pair<A, B>> compose(Monoid<A> ma, Monoid<B> mb) {
    return monoid(Semigroup.compose(ma, mb), pair(ma.zero(), mb.zero()), (n, p) -> pair(ma.multiply(n, p.left()), mb.multiply(n, p.right())));
  }

  /**
   * Return the dual Monoid.
   *
   * @param monoid a monoid.
   * @return a Monoid appending in reverse order,
   */
  static <A> Monoid<A> dual(Monoid<A> monoid) {
    return monoid(Semigroup.dual(monoid), monoid.zero(), monoid::multiply);
  }

  /**
   * Intersperses the given value between each two elements of the collection, and sums the result.
   *
   * @param monoid a monoid for A
   * @param as     An stream of values to append.
   * @param a      The value to intersperse between values of the given iterable.
   * @return The append of the given values and the interspersed value.
   */
  static <A> A intersperse(Monoid<A> monoid, final Iterable<? extends A> as, final A a) {
    return monoid.sum(Iterables.intersperse(as, a));
  }

}