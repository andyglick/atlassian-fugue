package com.atlassian.fugue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.Iterables.flatten;
import static java.util.Arrays.asList;

/**
 * {@link Semigroup} instances and factories.
 */
public final class Semigroups {

  /**
   * A semigroup that adds integers.
   */
  public static final Semigroup<Integer> intAdditionSemigroup = (i1, i2) -> i1 + i2;

  /**
   * A semigroup that adds doubles.
   */
  public static final Semigroup<Double> doubleAdditionSemigroup = (d1, d2) -> d1 + d2;

  /**
   * A semigroup that multiplies integers.
   */
  public static final Semigroup<Integer> intMultiplicationSemigroup = (i1, i2) -> i1 * i2;

  /**
   * A semigroup that yields the maximum of integers.
   */
  public static final Semigroup<Integer> intMaximumSemigroup = Math::max;

  /**
   * A semigroup that yields the minimum of integers.
   */
  public static final Semigroup<Integer> intMinimumSemigroup = Math::min;

  /**
   * A semigroup that adds big integers.
   */
  public static final Semigroup<BigInteger> bigintAdditionSemigroup = BigInteger::add;

  /**
   * A semigroup that multiplies big integers.
   */
  public static final Semigroup<BigInteger> bigintMultiplicationSemigroup = BigInteger::multiply;

  /**
   * A semigroup that yields the maximum of big integers.
   */
  public static final Semigroup<BigInteger> bigintMaximumSemigroup = BigInteger::max;

  /**
   * A semigroup that yields the minimum of big integers.
   */
  public static final Semigroup<BigInteger> bigintMinimumSemigroup = BigInteger::min;

  /**
   * A semigroup that yields the maximum of big decimals.
   */
  public static final Semigroup<BigDecimal> bigDecimalMaximumSemigroup = BigDecimal::max;

  /**
   * A semigroup that yields the minimum of big decimals.
   */
  public static final Semigroup<BigDecimal> bigDecimalMinimumSemigroup = BigDecimal::min;

  /**
   * A semigroup that adds longs.
   */
  public static final Semigroup<Long> longAdditionSemigroup = (x, y) -> x + y;

  /**
   * A semigroup that multiplies longs.
   */
  public static final Semigroup<Long> longMultiplicationSemigroup = (x, y) -> x * y;

  /**
   * A semigroup that yields the maximum of longs.
   */
  public static final Semigroup<Long> longMaximumSemigroup = Math::max;

  /**
   * A semigroup that yields the minimum of longs.
   */
  public static final Semigroup<Long> longMinimumSemigroup = Math::min;

  /**
   * A semigroup that ORs booleans.
   */
  public static final Semigroup<Boolean> disjunctionSemigroup = (b1, b2) -> b1 || b2;

  /**
   * A semigroup that XORs booleans.
   */
  public static final Semigroup<Boolean> exclusiveDisjunctionSemiGroup = (p, q) -> (p ^ q);

  /**
   * A semigroup that ANDs booleans.
   */
  public static final Semigroup<Boolean> conjunctionSemigroup = (b1, b2) -> b1 && b2;

  /**
   * A semigroup that appends strings.
   */
  public static final Semigroup<String> stringSemigroup = String::concat;

  /**
   * A semigroup for the Unit value.
   */
  public static final Semigroup<Unit> unitSemigroup = (u1, u2) -> Unit.VALUE;

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
  public static <A, B> Semigroup<Function<A, B>> functionSemigroup(final Semigroup<B> sb) {
    return (a1, a2) -> a -> sb.append(a1.apply(a), a2.apply(a));
  }

  /**
   * A semigroup that yields the maximum of by a comparator.
   *
   * @param comparator the comparator used to define the max of two value.
   * @return A max semigroup.
   */
  public static <A> Semigroup<A> maxSemigroup(final Comparator<A> comparator) {
    return (a1, a2) -> comparator.compare(a1, a2) < 0 ? a2 : a1;
  }

  /**
   * A semigroup that yields the minimum of by a comparator.
   *
   * @param comparator the comparator used to define the min of two value.
   * @return A min semigroup.
   */
  public static <A> Semigroup<A> minSemigroup(final Comparator<A> comparator) {
    return (a1, a2) -> comparator.compare(a1, a2) > 0 ? a2 : a1;
  }

  /**
   * A semigroup that yields the maximum of comparable values.
   *
   * @return A max semigroup.
   */
  public static <A extends Comparable<A>> Semigroup<A> maxSemigroup() {
    return (a1, a2) -> a1.compareTo(a2) < 0 ? a2 : a1;
  }

  /**
   * A semigroup that yields the minimum of comparable values.
   *
   * @return A min semigroup.
   */
  public static <A extends Comparable<A>> Semigroup<A> minSemigroup() {
    return (a1, a2) -> a1.compareTo(a2) > 0 ? a2 : a1;
  }

  /**
   * A semigroup for lists.
   *
   * @return A semigroup for lists.
   */
  public static <A> Semigroup<List<A>> listSemigroup() {
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
  public static <A> Semigroup<Iterable<A>> iterableSemigroup() {
    return (l1, l2) -> flatten(asList(l1, l2));
  }

  /**
   * A semigroup for option values (that take the first defined value).
   * *
   *
   * @return A semigroup for option values (that take the first defined value).
   */
  public static <A> Semigroup<Option<A>> firstOptionSemigroup() {
    return (a1, a2) -> a1.isDefined() ? a1 : a2;
  }

  /**
   * A semigroup for option values that take the last defined value.
   *
   * @return A semigroup for option values that take the last defined value.
   */
  public static <A> Semigroup<Option<A>> lastOptionSemigroup() {
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
  public static <L, R> Semigroup<Either<L, R>> eitherSemigroup(Semigroup<L> lS, Semigroup<R> rS) {
    return (e1, e2) -> e1.<Either<L, R>>fold(l1 -> e2.<Either<L, R>>fold(l2 -> left(lS.append(l1, l2)), r2 -> e1),
      r1 -> e2.<Either<L, R>>fold(l2 -> e2, r2 -> right(rS.append(r1, r2))));
  }

}
