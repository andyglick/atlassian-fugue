package com.atlassian.fugue;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;

import java.util.Iterator;

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

  /**
   * Zips two iterables into a single iterable that produces {@link Pair pairs}.
   * 
   * @param <A> LHS type
   * @param <B> RHS type
   * @param as left values
   * @param bs right values
   * @return an {@link Iterable iterable} of pairs, only as long as the shortest
   * input iterable.
   * @since 1.1
   */
  public static <A, B> Iterable<Pair<A, B>> zip(final Iterable<A> as, final Iterable<B> bs) {
    return new Zipper<A, B>(as, bs);
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

  /**
   * Iterable that produces pairs from two iterables that produce the left and
   * right elements.
   * 
   * @param <A> LHS type
   * @param <B> RHS type
   */
  static class Zipper<A, B> implements Iterable<Pair<A, B>> {
    private final Iterable<A> as;
    private final Iterable<B> bs;

    Zipper(final Iterable<A> as, final Iterable<B> bs) {
      this.as = checkNotNull(as, "as must not be null.");
      this.bs = checkNotNull(bs, "bs must not be null.");
    }

    @Override public Iterator<Pair<A, B>> iterator() {
      return new Iter();
    }

    class Iter implements Iterator<Pair<A, B>> {
      private final Iterator<A> a = checkNotNull(as.iterator(), "as iterator must not be null.");
      private final Iterator<B> b = checkNotNull(bs.iterator(), "bs iterator must not be null.");

      @Override public boolean hasNext() {
        return a.hasNext() && b.hasNext();
      }

      @Override public Pair<A, B> next() {
        return pair(a.next(), b.next());
      }

      @Override public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }
}
