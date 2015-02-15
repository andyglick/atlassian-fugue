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

import java.util.function.Function;
import java.util.function.Supplier;

import com.atlassian.fugue.mango.Function.Predicate;

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
   * @param <X> the exception type
   * @param <A> the value type
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
    return new Predicate<Either<L, R>>() {
      public Boolean apply(Either<L, R> e) {
        return e.isLeft();
      }
    };
  }

  /**
   * A predicate that tests if the supplied either is a right.
   * 
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @return the predicate testing right-hand-sidedness
   */
  public static <L, R> Predicate<Either<L, R>> isRight() {
    return new Predicate<Either<L, R>>() {
      public Boolean apply(Either<L, R> e) {
        return e.isRight();
      }
    };
  }

  /**
   * A function that maps an either to an option of its left type. The Function
   * will return a defined {@link Option} containing the either's left value if
   * {Either#isLeft()} is true, an undefined {@link Option} otherwise.
   * 
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @return the function returning a defined option for left-hand-sided eithers
   */
  public static <L, R> Function<Either<L, R>, Option<L>> leftMapper() {
    return new Function<Either<L, R>, Option<L>>() {
      public Option<L> apply(Either<L, R> either) {
        return either.left().toOption();
      }
    };
  }

  /**
   * A function that maps an either to an option of its right type. The Function
   * will return a defined {@link Option} containing the either's right value if
   * {Either#isRight()} is true, an undefined {@link Option} otherwise.
   * 
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @return the function returning a defined option for right-hand-sided
   * eithers
   */
  public static <L, R> Function<Either<L, R>, Option<R>> rightMapper() {
    return new Function<Either<L, R>, Option<R>>() {
      public Option<R> apply(Either<L, R> either) {
        return either.right().toOption();
      }
    };
  }

  public static <L, R> Function<L, Either<L, R>> toLeft() {
    return new Function<L, Either<L, R>>() {
      public Either<L, R> apply(final L from) {
        return Either.left(from);
      }
    };
  }

  // allows static import
  public static <L, R> Function<L, Either<L, R>> toLeft(final Class<L> leftType, final Class<R> rightType) {
    return Eithers.toLeft();
  }

  public static <L, R> Supplier<Either<L, R>> toLeft(final L l) {
    return Suppliers.compose(Eithers.<L, R> toLeft(), Suppliers.<L> ofInstance(l));
  }

  // allows static import
  public static <L, R> Supplier<Either<L, R>> toLeft(final L l, final Class<R> rightType) {
    return Eithers.toLeft(l);
  }

  public static <L, R> Function<R, Either<L, R>> toRight() {
    return new Function<R, Either<L, R>>() {
      public Either<L, R> apply(final R from) {
        return Either.right(from);
      }
    };
  }

  // allows static import
  public static <L, R> Function<R, Either<L, R>> toRight(final Class<L> leftType, final Class<R> rightType) {
    return Eithers.toRight();
  }

  public static <L, R> Supplier<Either<L, R>> toRight(final R r) {
    return Suppliers.compose(Eithers.<L, R> toRight(), Suppliers.<R> ofInstance(r));
  }

  // allows static import
  public static <L, R> Supplier<Either<L, R>> toRight(final Class<L> leftType, final R r) {
    return Eithers.toRight(r);
  }

  /**
   * Upcasts an {@link Either either} of left type L to an either of left type
   * LL, which is a super type of L, keeping the right type unchanged.
   * 
   * @param e the source either
   * @param <LL> the super type of the contained left type
   * @param <L> the contained left type
   * @param <R> the contained right type
   * @return an either of left type LL and right type R
   * @since 2.0
   */
  public static <LL, L extends LL, R> Either<LL, R> upcastLeft(Either<L, R> e) {
    return e.left().map(Functions.<LL> identity());
  }

  /**
   * Upcasts an {@link Either either} of right type R to an either of right type
   * RR, which is a super type of R, keeping the left type unchanged.
   * 
   * @param e the source either
   * @param <L> the contained left type
   * @param <RR> the super type of the contained right type
   * @param <R> the contained right type
   * @return an either of left type L and right type RR
   * @since 2.0
   */
  public static <L, RR, R extends RR> Either<L, RR> upcastRight(Either<L, R> e) {
    return e.right().map(Functions.<RR> identity());
  }
}
