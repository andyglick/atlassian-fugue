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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.function.Function;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;

/**
 * {@link Semigroup} instances.
 *
 * @see Monoids
 * @since 3.1
 */
public final class Semigroups {

  /**
   * A semigroup that yields the maximum of integers.
   */
  public static final Semigroup<Integer> intMaximum = Math::max;

  /**
   * A semigroup that yields the minimum of integers.
   */
  public static final Semigroup<Integer> intMinimum = Math::min;

  /**
   * A semigroup that yields the maximum of big integers.
   */
  public static final Semigroup<BigInteger> bigintMaximum = BigInteger::max;

  /**
   * A semigroup that yields the minimum of big integers.
   */
  public static final Semigroup<BigInteger> bigintMinimum = BigInteger::min;

  /**
   * A semigroup that yields the maximum of big decimals.
   */
  public static final Semigroup<BigDecimal> bigDecimalMaximum = BigDecimal::max;

  /**
   * A semigroup that yields the minimum of big decimals.
   */
  public static final Semigroup<BigDecimal> bigDecimalMinimum = BigDecimal::min;

  /**
   * A semigroup that yields the maximum of longs.
   */
  public static final Semigroup<Long> longMaximum = Math::max;

  /**
   * A semigroup that yields the minimum of longs.
   */
  public static final Semigroup<Long> longMinimum = Math::min;

  private Semigroups() {
  }

  public static <A> Semigroup<A> first() {
    return (x, y) -> x;
  }

  public static <A> Semigroup<A> last() {
    return (x, y) -> y;
  }

  /**
   * A semigroup for functions.
   *
   * @param sb The semigroup for the codomain.
   * @return A semigroup for functions.
   */
  public static <A, B> Semigroup<Function<A, B>> function(final Semigroup<B> sb) {
    return (a1, a2) -> a -> sb.append(a1.apply(a), a2.apply(a));
  }

  /**
   * A semigroup that yields the maximum of by a comparator.
   *
   * @param comparator the comparator used to define the max of two value.
   * @return A max semigroup.
   */
  public static <A> Semigroup<A> max(final Comparator<A> comparator) {
    return (a1, a2) -> comparator.compare(a1, a2) < 0 ? a2 : a1;
  }

  /**
   * A semigroup that yields the minimum of by a comparator.
   *
   * @param comparator the comparator used to define the min of two value.
   * @return A min semigroup.
   */
  public static <A> Semigroup<A> min(final Comparator<A> comparator) {
    return (a1, a2) -> comparator.compare(a1, a2) > 0 ? a2 : a1;
  }

  /**
   * A semigroup that yields the maximum of comparable values.
   *
   * @return A max semigroup.
   */
  public static <A extends Comparable<A>> Semigroup<A> max() {
    return (a1, a2) -> a1.compareTo(a2) < 0 ? a2 : a1;
  }

  /**
   * A semigroup that yields the minimum of comparable values.
   *
   * @return A min semigroup.
   */
  public static <A extends Comparable<A>> Semigroup<A> min() {
    return (a1, a2) -> a1.compareTo(a2) > 0 ? a2 : a1;
  }

  /**
   * Sums up values inside either, if both are left or right. Returns first left otherwise.
   * <ul>
   * <li>right(v1) + right(v2) → right(v1 + v2)</li>
   * <li>right(v1) + -left(v2) → left(v2)</li>
   * <li>left(v1) + right(v2) → left(v1)</li>
   * <li>left(v1) + left(v2) → left(v1 + v2)</li>
   * </ul>
   *
   * @param lS Semigroup for left values
   * @param rS Semigroup for right values
   * @return A semigroup that Sums up values inside either.
   */
  public static <L, R> Semigroup<Either<L, R>> either(Semigroup<L> lS, Semigroup<R> rS) {
    return (e1, e2) -> e1.<Either<L, R>>fold(l1 -> e2.<Either<L, R>>fold(l2 -> left(lS.append(l1, l2)), r2 -> e1),
      r1 -> e2.<Either<L, R>>fold(l2 -> e2, r2 -> right(rS.append(r1, r2))));
  }

}
