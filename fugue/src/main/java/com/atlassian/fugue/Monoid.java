package com.atlassian.fugue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A monoid abstraction to be defined across types of the given type argument. Implementations must follow the monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. sum(zero(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. sum(x, zero()) == x</li>
 * <li><em>Associativity</em>; forall  x y z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 */
public interface Monoid<A> extends Semigroup<A> {

  /**
   * The zero value for this monoid.
   *
   * @return The zero value for this monoid.
   */
  A zero();

  /**
   * Sums the given values.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public default A sum(final Iterable<A> as) {
    A m = zero();
    for (A a : as) {
      m = sum(m, a);
    }
    return m;
  }

  /**
   * Sums the given values.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public default A sum(final Stream<A> as) {
    return as.reduce(zero(), this);
  }

  /**
   * Returns a value summed <code>n</code> times (<code>a + a + ... + a</code>)
   *
   * @param n multiplier
   * @param a the value to multiply
   * @return <code>a</code> summed <code>n</code> times. If <code>n <= 0</code>, returns <code>zero()</code>
   */
  public default A multiply(final int n, final A a) {
    A m = zero();
    for (int i = 0; i < n; i++) {
      m = sum(m, a);
    }
    return m;
  }

  /**
   * Intersperses the given value between each two elements of the stream, and sums the result.
   *
   * @param as An stream of values to sum.
   * @param a  The value to intersperse between values of the given iterable.
   * @return The sum of the given values and the interspersed value.
   */
  public default A join(final Stream<A> as, final A a) {
    return as.reduce((a1, a2) -> sum(a1, sum(a, a2))).orElse(zero());
  }

  /**
   * Intersperses the given value between each two elements of the collection, and sums the result.
   *
   * @param as An stream of values to sum.
   * @param a  The value to intersperse between values of the given iterable.
   * @return The sum of the given values and the interspersed value.
   */
  public default A join(final Iterable<A> as, final A a) {
    return sum(Iterables.intersperse(as, a));
  }

  /**
   * Constructs a monoid from the given sum function and zero value, which must follow the monoidal laws.
   *
   * @param semigroup The semigroup for the monoid.
   * @param zero      The zero for the monoid.
   * @return A monoid instance that uses the given semigroup and zero value.
   */
  public static <A> Monoid<A> monoid(final Semigroup<A> semigroup, final A zero) {
    return new Monoid<A>() {

      @Override
      public A sum(final A a1, final A a2) {
        return semigroup.sum(a1, a2);
      }

      @Override
      public A zero() {
        return zero;
      }
    };
  }

}