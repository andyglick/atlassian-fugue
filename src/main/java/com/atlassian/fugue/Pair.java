package com.atlassian.fugue;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;

/**
 * Represents a pair of objects.
 * 
 * @since 1.0
 */
public final class Pair<A, B> {
  private static final int HALF_WORD = 16;

  /**
   * Factory method for static Pair growth.
   * 
   * @param left value, cannot be null
   * @param right value, cannot be null
   */
  public static <A, B> Pair<A, B> pair(final A left, final B right) {
    return new Pair<A, B>(left, right);
  }

  /**
   * Function for accessing the left value of {@link Pair pairs}.
   * 
   * @param A the left value type
   * @return a Function that given a {@link Pair} returns the left side value
   * @since 1.1
   */
  public static <A> Function<Pair<A, ?>, A> leftValue() {
    return new LeftAccessor<A>();
  }

  /**
   * Function for accessing the right value of {@link Pair pairs}.
   * 
   * @param B the right value type
   * @return a Function that given a {@link Pair} returns the right side value
   * @since 1.1
   */
  public static <B> Function<Pair<?, B>, B> rightValue() {
    return new RightAccessor<B>();
  }

  //
  // members
  //

  private final A left;
  private final B right;

  /**
   * @param left value, cannot be null
   * @param right value, cannot be null
   */
  public Pair(final A left, final B right) {
    this.left = checkNotNull(left, "Left parameter must not be null.");
    this.right = checkNotNull(right, "Right parameter must not be null.");
  }

  public A left() {
    return left;
  }

  public B right() {
    return right;
  }

  @Override public String toString() {
    return "Pair(" + left + ", " + right + ")";
  }

  @Override public boolean equals(final Object o) {
    if (o == null) {
      return false;
    }
    if (this == o) {
      return true;
    }

    if (!(o instanceof Pair<?, ?>)) {
      return false;
    }
    final Pair<?, ?> that = (Pair<?, ?>) o;
    return left.equals(that.left) && right.equals(that.right);
  }

  @Override public int hashCode() {
    final int lh = left.hashCode();
    final int rh = right.hashCode();
    return (((lh >> HALF_WORD) ^ lh) << HALF_WORD) | (((rh << HALF_WORD) ^ rh) >> HALF_WORD);
  }

  //
  // inner classes
  //

  static class LeftAccessor<A> implements Function<Pair<A, ?>, A> {
    @Override public A apply(final Pair<A, ?> from) {
      return from.left();
    }
  }

  static class RightAccessor<B> implements Function<Pair<?, B>, B> {
    @Override public B apply(final Pair<?, B> from) {
      return from.right();
    }
  }
}
