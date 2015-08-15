package com.atlassian.fugue;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * Implementations must satisfy the law of associativity:
 * <ul>
 * <li><em>Associativity</em>; forall  x y z. append(append(x, y), z) == append(x, append(y, z))</li>
 * </ul>
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
   * Composes this semigroup with another.
   */
  default <B> Semigroup<Pair<A, B>> composeSemigroup(Semigroup<B> sb) {
    return (ab1, ab2) -> Pair.pair(append(ab1.left(), ab2.left()), sb.append(ab1.right(), ab2.right()));
  }

  /**
   * @return a semigroup appending in reverse order
   */
  default Semigroup<A> flipped() {
    return (a1, a2) -> append(a2, a1);
  }

  /**
   * Apply method to conform to the {@link BinaryOperator} interface.
   *
   * @deprecated use {@link #append(Object, Object)} directly
   */
  @Override @Deprecated default A apply(final A a1, final A a2) {
    return append(a1, a2);
  }

  static <A> Semigroup<A> semigroup(BiFunction<A, A, A> operator) {
    return operator::apply;
  }

}
