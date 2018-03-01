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

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static io.atlassian.fugue.Iterables.collect;
import static io.atlassian.fugue.Iterables.map;
import static io.atlassian.fugue.Suppliers.compose;
import static io.atlassian.fugue.Suppliers.ofInstance;

/**
 * Utility functions for Eithers.
 *
 * @since 1.2
 */
public class Eithers {

  // /CLOVER:OFF

  private Eithers() {}

  // /CLOVER:ON

  /**
   * Extracts an object from an Either, regardless of the side in which it is
   * stored, provided both sides contain the same type. This method will never
   * return null.
   *
   * @param <T> the type for both the LHS and the RHS
   * @param either use whichever side holds the value to return
   * @return the value from whichever side holds it
   */
  public static <T> T merge(final Either<T, T> either) {
    if (either.isLeft()) {
      return either.left().get();
    }
    return either.right().get();
  }

  /**
   * Creates an Either based on a boolean expression. If predicate is true, a
   * Right will be returned containing the supplied right value; if it is false,
   * a Left will be returned containing the supplied left value.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param predicate if predicate is true, a Right will be returned if it is
   * false, a Left will be returned containing the supplied left value.
   * @param left the LHS value
   * @param right the RHS value
   * @return an either with the appropriately selected value
   */
  public static <L, R> Either<L, R> cond(final boolean predicate, final L left, final R right) {
    return predicate ? Either.<L, R> right(right) : Either.<L, R> left(left);
  }

  /**
   * Simplifies extracting a value or throwing a checked exception from an
   * Either.
   *
   * @param <A> the value type
   * @param <X> the exception type
   * @param either to extract from
   * @return the value from the RHS
   * @throws X the exception on the LHS
   */
  public static <X extends Exception, A> A getOrThrow(final Either<X, A> either) throws X {
    if (either.isLeft()) {
      throw either.left().get();
    }
    return either.right().get();
  }

  /**
   * A predicate that tests if the supplied either is a left.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @return the predicate testing left-hand-sidedness
   */
  public static <L, R> Predicate<Either<L, R>> isLeft() {
    return Either::isLeft;
  }

  /**
   * A predicate that tests if the supplied either is a right.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @return the predicate testing right-hand-sidedness
   */
  public static <L, R> Predicate<Either<L, R>> isRight() {
    return Either::isRight;
  }

  /**
   * A function that maps an either to an option of its left type. The Function
   * will return a defined {@link io.atlassian.fugue.Option} containing the
   * either's left value if {Either#isLeft()} is true, an undefined
   * {@link io.atlassian.fugue.Option} otherwise.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @return the function returning a defined option for left-hand-sided eithers
   */
  public static <L, R> Function<Either<L, R>, Option<L>> leftMapper() {
    return either -> either.left().toOption();
  }

  /**
   * A function that maps an either to an option of its right type. The Function
   * will return a defined {@link io.atlassian.fugue.Option} containing the
   * either's right value if {Either#isRight()} is true, an undefined
   * {@link io.atlassian.fugue.Option} otherwise.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @return the function returning a defined option for right-hand-sided
   * eithers
   */
  public static <L, R> Function<Either<L, R>, Option<R>> rightMapper() {
    return either -> either.right().toOption();
  }

  /**
   * Function to convert from an value to a
   * {@link io.atlassian.fugue.Either.Left} containing that value.
   *
   * @param <L> left type.
   * @param <R> right type.
   * @return a {@link java.util.function.Function} returning a
   * {@link io.atlassian.fugue.Either.Left}.
   */
  public static <L, R> Function<L, Either<L, R>> toLeft() {
    return Either::left;
  }

  /**
   * Function to convert from a value to a
   * {@link io.atlassian.fugue.Either.Left} containing that value. Allows
   * hinting the correct types.
   *
   * @param leftType expected left type.
   * @param rightType expected right type.
   * @param <L> left type.
   * @param <R> right type.
   * @return a {@link java.util.function.Function} returning a
   * {@link io.atlassian.fugue.Either.Left}.
   */
  public static <L, R> Function<L, Either<L, R>> toLeft(final Class<L> leftType, final Class<R> rightType) {
    return Eithers.toLeft();
  }

  /**
   * Supplier returning a {@link io.atlassian.fugue.Either.Left}.
   *
   * @param l value to return inside the left.
   * @param <L> left type.
   * @param <R> right type.
   * @return a {@link java.util.function.Supplier} returning a
   * {@link io.atlassian.fugue.Either.Left}..
   */
  public static <L, R> Supplier<Either<L, R>> toLeft(final L l) {
    return compose(Eithers.<L, R> toLeft(), ofInstance(l));
  }

  /**
   * Supplier returning a {@link io.atlassian.fugue.Either.Left}. Allows hinting
   * the correct right type.
   *
   * @param l value to return inside the left.
   * @param rightType type hint for the right type of the either.
   * @param <L> left type.
   * @param <R> right type.
   * @return a {@link java.util.function.Supplier} returning a
   * {@link io.atlassian.fugue.Either.Left}.
   */
  public static <L, R> Supplier<Either<L, R>> toLeft(final L l, final Class<R> rightType) {
    return Eithers.toLeft(l);
  }

  /**
   * Function to convert from an value to a
   * {@link io.atlassian.fugue.Either.Right}. Allows hinting the correct types.
   *
   * @param <L> left type.
   * @param <R> right type.
   * @return a {@link java.util.function.Function} returning a
   * {@link io.atlassian.fugue.Either.Right}.
   */
  public static <L, R> Function<R, Either<L, R>> toRight() {
    return Either::right;
  }

  /**
   * Function to convert from a value to a
   * {@link io.atlassian.fugue.Either.Right} containing that value. Allows
   * hinting the correct types.
   *
   * @param leftType expected left type.
   * @param rightType expected right type.
   * @param <L> left type.
   * @param <R> right type.
   * @return a {@link java.util.function.Function} returning a
   * {@link io.atlassian.fugue.Either.Right}.
   */
  public static <L, R> Function<R, Either<L, R>> toRight(final Class<L> leftType, final Class<R> rightType) {
    return Eithers.toRight();
  }

  /**
   * Supplier returning a {@link io.atlassian.fugue.Either.Right}.
   *
   * @param r value to return inside the right.
   * @param <L> left type.
   * @param <R> right type.
   * @return a {@link java.util.function.Supplier} returning a
   * {@link io.atlassian.fugue.Either.Right}..
   */
  public static <L, R> Supplier<Either<L, R>> toRight(final R r) {
    return compose(Eithers.<L, R> toRight(), ofInstance(r));
  }

  /**
   * Supplier returning a {@link io.atlassian.fugue.Either.Right}. Allows
   * hinting the correct right type.
   *
   * @param r value to return inside the right.
   * @param leftType type hint for the left type of the either.
   * @param <L> left type.
   * @param <R> right type.
   * @return a {@link java.util.function.Supplier} returning a
   * {@link io.atlassian.fugue.Either.Right}.
   */
  public static <L, R> Supplier<Either<L, R>> toRight(final Class<L> leftType, final R r) {
    return Eithers.toRight(r);
  }

  /**
   * Upcasts an {@link Either either} of left type L to an either of left type
   * LL, which is a super type of L, keeping the right type unchanged.
   *
   * @param e the source either
   * @param <L> the base type to upcast
   * @param <LL> the super type of the contained left type
   * @param <R> the contained right type
   * @return an either of left type LL and right type R
   * @since 2.0
   */
  public static <LL, L extends LL, R> Either<LL, R> upcastLeft(final Either<L, R> e) {
    return e.left().map(Functions.<LL> identity());
  }

  /**
   * Upcasts an {@link Either either} of right type R to an either of right type
   * RR, which is a super type of R, keeping the left type unchanged.
   *
   * @param e the source either
   * @param <L> the contained left type
   * @param <R> the base type to upcast
   * @param <RR> the super type of the contained right type
   * @return an either of left type L and right type RR
   * @since 2.0
   */
  public static <L, RR, R extends RR> Either<L, RR> upcastRight(final Either<L, R> e) {
    return e.right().map(Functions.<RR> identity());
  }

  /**
   * Takes an {@link java.lang.Iterable} of {@link Either eithers}, and collects
   * the left values of every either which has a left value
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param it iterable of eithers to filter and transform from
   * @return the left values contained in the contents of it
   */
  public static <L, R> Iterable<L> filterLeft(final Iterable<Either<L, R>> it) {
    return collect(it, Eithers.<L, R> leftMapper());
  }

  /**
   * Takes an {@link java.lang.Iterable} of {@link Either eithers}, and collects
   * the right values of every either which has a left value
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param it iterable of eithers to filter and transform from
   * @return the right values contained in the contents of it
   */
  public static <L, R> Iterable<R> filterRight(final Iterable<Either<L, R>> it) {
    return Options.flatten(map(it, Eithers.<L, R> rightMapper()));
  }

  /**
   * Collect the right values if there are only rights, otherwise return the
   * first left encountered.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param eithers an Iterable of either values
   * @return either the iterable of right values, or the first left encountered.
   */
  public static <L, R> Either<L, Iterable<R>> sequenceRight(final Iterable<Either<L, R>> eithers) {
    return sequenceRight(eithers, Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Collect the right values if there are only rights, otherwise return the
   * first left encountered.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param <A> The intermediate accumulator type
   * @param <C> The result type
   * @param eithers an Iterable of either values
   * @param collector result collector
   * @since 4.6.0
   * @return either the iterable of right values, or the first left encountered.
   */
  public static <L, R, A, C> Either<L, C> sequenceRight(final Iterable<Either<L, R>> eithers, final Collector<R, A, C> collector) {
    final A accumulator = collector.supplier().get();
    for (final Either<L, R> e : eithers) {
      if (e.isLeft()) {
        return e.left().as();
      }
      collector.accumulator().accept(accumulator, e.right().get());
    }
    return Either.right(collector.finisher().apply(accumulator));
  }

  /**
   * Collect the left values if there are only lefts, otherwise return the first
   * right encountered.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param eithers an Iterable of either values
   * @return either the iterable of left values, or the first right encountered.
   */
  public static <L, R> Either<Iterable<L>, R> sequenceLeft(final Iterable<Either<L, R>> eithers) {
    return sequenceLeft(eithers, Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Collect the left values if there are only lefts, otherwise return the first
   * right encountered.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param <A> The intermediate accumulator type
   * @param <C> The result type
   * @param eithers an Iterable of either values
   * @param collector result collector
   * @since 4.6.0
   * @return either the iterable of left values, or the first right encountered.
   */
  public static <L, R, A, C> Either<C, R> sequenceLeft(final Iterable<Either<L, R>> eithers, final Collector<L, A, C> collector) {
    final A accumulator = collector.supplier().get();
    for (final Either<L, R> e : eithers) {
      if (e.isRight()) {
        return e.right().as();
      }
      collector.accumulator().accept(accumulator, e.left().get());
    }
    return Either.left(collector.finisher().apply(accumulator));
  }
}
