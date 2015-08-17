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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.Iterables.join;
import static java.util.Arrays.asList;

/**
 * {@link Semigroup} instances.
 *
 * @since 3.0
 */
public final class Semigroups {

  /**
   * A semigroup that adds integers.
   */
  public static final Semigroup<Integer> intAddition = (i1, i2) -> i1 + i2;

  /**
   * A semigroup that adds doubles.
   */
  public static final Semigroup<Double> doubleAddition = (d1, d2) -> d1 + d2;

  /**
   * A semigroup that multiplies integers.
   */
  public static final Semigroup<Integer> intMultiplication = (i1, i2) -> i1 * i2;

  /**
   * A semigroup that yields the maximum of integers.
   */
  public static final Semigroup<Integer> intMaximum = Math::max;

  /**
   * A semigroup that yields the minimum of integers.
   */
  public static final Semigroup<Integer> intMinimum = Math::min;

  /**
   * A semigroup that adds big integers.
   */
  public static final Semigroup<BigInteger> bigintAddition = BigInteger::add;

  /**
   * A semigroup that multiplies big integers.
   */
  public static final Semigroup<BigInteger> bigintMultiplication = BigInteger::multiply;

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
   * A semigroup that adds longs.
   */
  public static final Semigroup<Long> longAddition = (x, y) -> x + y;

  /**
   * A semigroup that multiplies longs.
   */
  public static final Semigroup<Long> longMultiplication = (x, y) -> x * y;

  /**
   * A semigroup that yields the maximum of longs.
   */
  public static final Semigroup<Long> longMaximum = Math::max;

  /**
   * A semigroup that yields the minimum of longs.
   */
  public static final Semigroup<Long> longMinimum = Math::min;

  /**
   * A semigroup that ORs booleans.
   */
  public static final Semigroup<Boolean> disjunction = (b1, b2) -> b1 || b2;

  /**
   * A semigroup that XORs booleans.
   */
  public static final Semigroup<Boolean> exclusiveDisjunction = (p, q) -> (p ^ q);

  /**
   * A semigroup that ANDs booleans.
   */
  public static final Semigroup<Boolean> conjunction = (b1, b2) -> b1 && b2;

  /**
   * A semigroup that appends strings.
   */
  public static final Semigroup<String> string = String::concat;

  /**
   * A semigroup for the Unit value.
   */
  public static final Semigroup<Unit> unit = (u1, u2) -> Unit.VALUE;

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
   * A semigroup for lists.
   *
   * @return A semigroup for lists.
   */
  public static <A> Semigroup<List<A>> list() {
    return (l1, l2) -> {
      final List<A> sumList;
      if (l1.isEmpty()) {
        sumList = l2;

      } else if (l2.isEmpty()) {
        sumList = l1;

      } else {
        sumList = new ArrayList<>(l1.size() + l2.size());
        sumList.addAll(l1);
        sumList.addAll(l2);
      }
      return sumList;
    };
  }

  /**
   * A semigroup for iterables.
   *
   * @return A semigroup for iterables.
   */
  public static <A> Semigroup<Iterable<A>> iterable() {
    return (l1, l2) -> join(asList(l1, l2));
  }

  /**
   * A semigroup for option values (that take the first defined value).
   * *
   *
   * @return A semigroup for option values (that take the first defined value).
   */
  public static <A> Semigroup<Option<A>> firstOption() {
    return (a1, a2) -> a1.isDefined() ? a1 : a2;
  }

  /**
   * A semigroup for option values that take the last defined value.
   *
   * @return A semigroup for option values that take the last defined value.
   */
  public static <A> Semigroup<Option<A>> lastOption() {
    return (a1, a2) -> a2.isDefined() ? a2 : a1;
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
