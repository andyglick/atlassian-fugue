/*
   Copyright 2011 Atlassian

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
   * Factory method for a Pair factory function.
   */
  public static <A, B> Function2<A, B, Pair<A, B>> pairs() {
    return new Function2<A, B, Pair<A, B>>() {
      public com.atlassian.fugue.Pair<A, B> apply(final A a, final B b) {
        return pair(a, b);
      }
    };
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
    return Iterables.zip(as, bs);
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
