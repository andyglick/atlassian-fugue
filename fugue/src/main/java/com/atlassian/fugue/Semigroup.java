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

}
