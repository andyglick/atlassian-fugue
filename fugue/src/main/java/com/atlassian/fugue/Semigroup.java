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

import java.util.function.BinaryOperator;
import java.util.function.Function;

import static com.atlassian.fugue.Functions.fold;

/**
 * A Semigroup is an algebraic structure consisting of an associative binary operation across the values of a given type (the Semigroup type argument).
 * Implementations must satisfy the law of associativity:
 * <ul>
 * <li><em>Associativity</em>; forall  x y z. append(append(x, y), z) == append(x, append(y, z))</li>
 * </ul>
 *
 * @since 3.0
 */
@FunctionalInterface public interface Semigroup<A> extends BinaryOperator<A> {

  /**
   * Combine the two given arguments.
   *
   * @param a1 left value to combine
   * @param a2 right value to combine
   * @return the combination of the left and right value.
   */
  A append(final A a1, final A a2);

  /**
   * Reduce a 'non-empty list' with {@link #append(Object, Object)}
   *
   * @param head the head of the non-empty
   * @param tail the tail of non-empty
   * @return the sum of all elements.
   */
  default A sumNel(A head, Iterable<A> tail) {
    return fold(this, head, tail);
  }

  /**
   * Returns a value summed <code>n + 1</code> times (<code>a + a + ... + a</code>)
   * The default definition uses peasant multiplication, exploiting associativity to only
   * require `O(log n)` uses of {@link #append(Object, Object)}.
   *
   * @param n multiplier
   * @param a the value to be reapeatly summed n + 1 times
   * @return <code>a</code> summed <code>n</code> times. If <code>n <= 0</code>, returns <code>zero()</code>
   */
  default A multiply1p(int n, A a) {
    if (n<=0) {
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
   * Apply method to conform to the {@link BinaryOperator} interface.
   *
   * @deprecated use {@link #append(Object, Object)} directly
   */
  @Override @Deprecated default A apply(final A a1, final A a2) {
    return append(a1, a2);
  }

  /**
   * Construct a semigroup from a curried associative binary operator
   *
   * @param binaryOperator a curried binary operator
   * @return The semigroup yielding from the operator.
   */
  static <A> Semigroup<A> semigroup(Function<A, Function<A, A>> binaryOperator) {
    return (a1, a2) -> binaryOperator.apply(a1).apply(a2);
  }

  /**
   * Composes a semigroup with another.
   */
  static <A, B> Semigroup<Pair<A, B>> compose(Semigroup<A> sa, Semigroup<B> sb) {
    return (ab1, ab2) -> Pair.pair(sa.append(ab1.left(), ab2.left()), sb.append(ab1.right(), ab2.right()));
  }

  /**
   * Return the dual Semigroup of a semigroup
   *
   * @param semigroup
   * @return a semigroup appending in reverse order
   */
  static <A> Semigroup<A> dual(Semigroup<A> semigroup) {
    return (a1, a2) -> semigroup.append(a2, a1);
  }

}
