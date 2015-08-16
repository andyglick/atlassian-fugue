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

/**
 * A Monoid is an algebraic structure consisting of an associative binary operation across the values of a given type (a monoid is a {@link Semigroup})
 * and an identity element for this operation.
 * Implementations must follow the monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. append(empty(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. append(x, empty()) == x</li>
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
  A empty();

  /**
   * Sums the given values.
   *
   * @param as The values to append.
   * @return The append of the given values.
   */
  default A concat(final Iterable<A> as) {
    A m = empty();
    for (A a : as) {
      m = append(m, a);
    }
    return m;
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