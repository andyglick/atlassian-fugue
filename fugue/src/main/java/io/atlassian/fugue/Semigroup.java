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

import static io.atlassian.fugue.Pair.pair;

/**
 * A Semigroup is an algebraic structure consisting of an associative binary
 * operation across the values of a given type (the Semigroup type argument).
 * Implementations must satisfy the law of associativity:
 * <ul>
 * <li><em>Associativity</em>; forall x y z. append(append(x, y), z) ==
 * append(x, append(y, z))</li>
 * </ul>
 * Methods {@link #sumNonEmpty(Object, Iterable)} and
 * {@link #multiply1p(int, Object)} can be overriden for performance reason,
 * especially if {@link #sumNonEmpty(Object, Iterable)} can be implemented to
 * not require evaluation of the whole iterable.
 *
 * @see Monoid
 * @since 3.1
 */
public interface Semigroup<A> {

  /**
   * Combine the two given arguments.
   *
   * @param a1 left value to combine
   * @param a2 right value to combine
   * @return the combination of the left and right value.
   */
  A append(final A a1, final A a2);

  /**
   * Reduce a 'non-empty' Iterable with {@link #append(Object, Object)}
   *
   * @param head the head of the 'non-empty' Iterable
   * @param tail the tail (maybe an empty Iterable).
   * @return the sum of all elements.
   */
  default A sumNonEmpty(A head, Iterable<A> tail) {

    A currentValue = head;
    for (final A a : tail) {
      currentValue = append(currentValue, a);
    }
    return currentValue;

  }

  /**
   * Returns a value summed <code>n + 1</code> times (
   * <code>a + a + ... + a</code>) The default definition uses peasant
   * multiplication, exploiting associativity to only require `O(log n)` uses of
   * {@link #append(Object, Object)}.
   *
   * @param n multiplier
   * @param a the value to be reapeatly summed n + 1 times
   * @return <code>a</code> summed <code>n</code> times. If <code>n <= 0</code>,
   * returns <code>zero()</code>
   */
  default A multiply1p(int n, A a) {
    if (n <= 0) {
      return a;
    }

    A xTmp = a;
    int yTmp = n;
    A zTmp = a;
    while (true) {
      if ((yTmp & 1) == 1) {
        zTmp = append(xTmp, zTmp);
        if (yTmp == 1) {
          return zTmp;
        }
      }
      xTmp = append(xTmp, xTmp);
      yTmp = (yTmp) >>> 1;
    }
  }

  /**
   * Composes a semigroup with another.
   */
  static <A, B> Semigroup<Pair<A, B>> compose(Semigroup<A> sa, Semigroup<B> sb) {
    return new Semigroup<Pair<A, B>>() {
      @Override public Pair<A, B> append(Pair<A, B> ab1, Pair<A, B> ab2) {
        return pair(sa.append(ab1.left(), ab2.left()), sb.append(ab1.right(), ab2.right()));
      }

      @Override public Pair<A, B> multiply1p(int n, Pair<A, B> ab) {
        return pair(sa.multiply1p(n, ab.left()), sb.multiply1p(n, ab.right()));
      }
    };
  }

  /**
   * Return the dual Semigroup of a semigroup
   *
   * @param semigroup
   * @return a semigroup appending in reverse order
   */
  static <A> Semigroup<A> dual(Semigroup<A> semigroup) {
    return new Semigroup<A>() {
      @Override public A append(A a1, A a2) {
        return semigroup.append(a2, a1);
      }

      @Override public A multiply1p(int n, A a) {
        return semigroup.multiply1p(n, a);
      }
    };
  }

}
