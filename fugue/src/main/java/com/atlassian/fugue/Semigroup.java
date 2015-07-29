package com.atlassian.fugue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Implementations must satisfy the law of associativity:
 * <ul>
 * <li><em>Associativity</em>; forall  x y z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 */
@FunctionalInterface
public interface Semigroup<A> extends BinaryOperator<A> {

  /**
   * Sums the two given arguments.
   *
   * @param a1 A value to sum with another.
   * @param a2 A value to sum with another.
   * @return The sum of the two given arguments.
   */
  A sum(final A a1, final A a2);

  /**
   * Returns a function that sums the given value according to this semigroup.
   *
   * @param a1 The value to sum.
   * @return A function that sums the given value according to this semigroup.
   */
  default UnaryOperator<A> sum(final A a1) {
    return a2 -> sum(a1, a2);
  }

  /**
   * Apply method to conform to the {@link BinaryOperator} interface.
   * @deprecated use {@link #sum(Object, Object)} directly
   */
  @Override
  @Deprecated
  default A apply(final A a1, final A a2) {
    return sum(a1, a2);
  }

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
   * A semigroup that multiplies doubles.
   */
  public static final Semigroup<Double> doubleMultiplicationSemigroup = (d1, d2) -> d1 * d2;

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
   * A semigroup that adds big decimals.
   */
  public static final Semigroup<BigDecimal> bigdecimalAdditionSemigroup = BigDecimal::add;

  /**
   * A semigroup that multiplies big decimals.
   */
  public static final Semigroup<BigDecimal> bigdecimalMultiplicationSemigroup = BigDecimal::multiply;

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
   * A semigroup for functions.
   *
   * @param sb The semigroup for the codomain.
   * @return A semigroup for functions.
   */
  public static <A, B> Semigroup<Function<A, B>> functionSemigroup(final Semigroup<B> sb) {
    return (a1, a2) -> a -> sb.sum(a1.apply(a), a2.apply(a));
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
    return (l1, l2) -> Iterables.flatten(Arrays.asList(l1, l2));
  }

  /**
   * A semigroup for option values (that take the first defined value).
   * *
   *
   * @return A semigroup for option values (that take the first defined value).
   */
  public static <A> Semigroup<Option<A>> optionSemigroup() {
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
   * A semigroup for streams.
   *
   * @return A semigroup for streams.
   */
  public static <A> Semigroup<Stream<A>> streamSemigroup() {
    return Stream::concat;
  }

  /**
   * A semigroup for unary products.
   *
   * @param sa A semigroup for the product's type.
   * @return A semigroup the unary product.
   */
  public static <P, A> Semigroup<P> p1Semigroup(final Function<P, A> getA, final Semigroup<A> sa, final Function<A, P> toP) {
    return (p1, p2) -> toP.apply(sa.sum(getA.apply(p1), getA.apply(p2)));
  }

  /**
   * A semigroup for binary products.
   *
   * @param sa A semigroup for the product's first type.
   * @param sb A semigroup for the product's second type.
   * @return A semigroup for the binary product.
   */
  public static <P, A, B> Semigroup<P> p2Semigroup(final Function<P, A> getA, final Semigroup<A> sa, final Function<P, B> getB, final Semigroup<B> sb,
    final BiFunction<A, B, P> toP) {
    return (p1, p2) -> toP.apply(sa.sum(getA.apply(p1), getA.apply(p2)), sb.sum(getB.apply(p1), getB.apply(p2)));
  }

  /**
   * A semigroup for {@link Pair}
   *
   * @param sa A semigroup for the pair left type.
   * @param sb A semigroup for the pair right type.
   * @return A semigroup a pair.
   */
  public static <A, B> Semigroup<Pair<A, B>> pairSemigroup(final Semigroup<A> sa, final Semigroup<B> sb) {
    return p2Semigroup(Pair::left, sa, Pair::right, sb, Pair::pair);
  }

  /**
   * A semigroup for the Unit value.
   */
  public static final Semigroup<Unit> unitSemigroup = (u1, u2) -> Unit.VALUE;

}
