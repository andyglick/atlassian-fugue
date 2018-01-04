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
package io.atlassian.fugue;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Represents a pair of objects.
 *
 * @since 1.0
 */
public final class Pair<A, B> implements Serializable {
  private static final long serialVersionUID = 3054071035067921893L;

  private static final int HALF_WORD = 16;

  /**
   * Factory method for static Pair growth.
   *
   * @param <A> the left value type
   * @param <B> the right value type
   * @param left value, cannot be null
   * @param right value, cannot be null
   * @return the Pair containing the passed values
   */
  public static <A, B> Pair<A, B> pair(final A left, final B right) {
    return new Pair<>(left, right);
  }

  /**
   * Factory method for a Pair factory function.
   *
   * @param <A> the left value type
   * @param <B> the right value type
   * @return a function that constructs Pairs
   */
  public static <A, B> BiFunction<A, B, Pair<A, B>> pairs() {
    return Pair::pair;
  }

  /**
   * Function for accessing the left value of {@link Pair pairs}.
   *
   * @param <A> the left value type
   * @return a Function that given a {@link io.atlassian.fugue.Pair} returns the
   * left side value
   * @since 1.1
   */
  public static <A> Function<Pair<A, ?>, A> leftValue() {
    return Pair::left;
  }

  /**
   * Function for accessing the right value of {@link Pair pairs}.
   *
   * @param <B> the right value type
   * @return a Function that given a {@link io.atlassian.fugue.Pair} returns the
   * right side value
   * @since 1.1
   */
  public static <B> Function<Pair<?, B>, B> rightValue() {
    return Pair::right;
  }

  /**
   * Performs function application within an homogeneous pair (applicative
   * functor pattern).
   *
   * @param aa an homogeneous pair
   * @param ff The pair of functions to apply.
   * @return A new pair after applying the given pair of functions through aa.
   */
  public static <A, B> Pair<B, B> ap(final Pair<A, A> aa, final Pair<Function<A, B>, Function<A, B>> ff) {
    return Pair.pair(ff.left().apply(aa.left()), ff.right().apply(aa.right()));
  }

  /**
   * Apply a function to both elements of an homogeneous pair.
   *
   * @param aa an homogeneous pair
   * @param f function to apply to both elements of aa
   * @return A new pair after applying the function to aa elements.
   */
  public static <A, B> Pair<B, B> map(final Pair<A, A> aa, final Function<A, B> f) {
    return Pair.pair(f.apply(aa.left()), f.apply(aa.right()));
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

  /**
   * Zips the two given optionals into an optional of a pair.
   *
   * @param oA the first optional
   * @param oB the second optional
   * @param <A> the first type
   * @param <B> the second type
   * @return empty if either or both optionals are empty
   * @since 4.6.0
   */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType") public static <A, B> Optional<Pair<A, B>> zip(final Optional<A> oA, final Optional<B> oB) {
    if (oA.isPresent() && oB.isPresent()) {
      return Optional.of(pair(oA.get(), oB.get()));
    }
    return Optional.empty();
  }

  //
  // members
  //

  private final A left;
  private final B right;

  /**
   * Constructor for Pair.
   *
   * @param left value, cannot be null
   * @param right value, cannot be null
   */
  public Pair(final A left, final B right) {
    this.left = requireNonNull(left, "Left parameter must not be null.");
    this.right = requireNonNull(right, "Right parameter must not be null.");
  }

  /**
   * Accessor method for the left value of the pair.
   *
   * @return a A object.
   */
  public A left() {
    return left;
  }

  /**
   * Accessor method for the right value of the pair.q
   *
   * @return a B object.
   */
  public B right() {
    return right;
  }

  /** {@inheritDoc} */
  @Override public String toString() {
    return "Pair(" + left + ", " + right + ")";
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override public int hashCode() {
    final int lh = left.hashCode();
    final int rh = right.hashCode();
    return (((lh >> HALF_WORD) ^ lh) << HALF_WORD) | (((rh << HALF_WORD) ^ rh) >> HALF_WORD);
  }
}
