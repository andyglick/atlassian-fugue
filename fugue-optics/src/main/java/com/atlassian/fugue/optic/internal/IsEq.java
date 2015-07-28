package com.atlassian.fugue.optic.internal;

import java.util.function.BiFunction;

/**
 * Represents two values of the same type that are expected to be equal.
 */
public final class IsEq<A> {

  private final A lhs;

  private final A rhs;

  public IsEq(A lhs, A rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public <R> R match(BiFunction<A, A, R> cases) {
    return cases.apply(lhs, rhs);
  }

  /**
   * @return left hand side,
   */
  public A lhs() {
    return lhs;
  }

  /**
   * @return right hand side,
   */
  public A rhs() {
    return rhs;
  }

  public static <A> IsEq<A> isEq(A lhs, A rhs) {
    return new IsEq<>(lhs, rhs);
  }
}
