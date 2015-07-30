package com.atlassian.fugue;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * Implementations must satisfy the law of associativity:
 * <ul>
 * <li><em>Associativity</em>; forall  x y z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 */
@FunctionalInterface public interface Semigroup<A> extends BinaryOperator<A> {

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
   * Composes this semigroup with another.
   */
  default <B> Semigroup<Pair<A, B>> composeSemigroup(Semigroup<B> sb) {
    return (ab1, ab2) -> Pair.pair(sum(ab1.left(), ab2.left()), sb.sum(ab1.right(), ab2.right()));
  }

  /**
   * Apply method to conform to the {@link BinaryOperator} interface.
   *
   * @deprecated use {@link #sum(Object, Object)} directly
   */
  @Override @Deprecated default A apply(final A a1, final A a2) {
    return sum(a1, a2);
  }

  static <A> Semigroup<A> semigroup(BiFunction<A, A, A> operator) {
    return operator::apply;
  }

}
