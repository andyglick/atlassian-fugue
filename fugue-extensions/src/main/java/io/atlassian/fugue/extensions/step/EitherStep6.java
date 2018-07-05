package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.extensions.functions.Function6;
import io.atlassian.fugue.extensions.functions.Predicate6;

import java.util.function.Supplier;

/**
 * The sixth step of the {@link Either} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Either)}
 *
 * @param <A> The right hand side type of the first defined right value
 * @param <B> The right hand side type of the second defined right value
 * @param <C> The right hand side type of the third defined right value
 * @param <D> The right hand side type of the fourth defined right value
 * @param <E> The right hand side type of the fifth defined right value
 * @param <F> The right hand side type of the sixth defined right value
 * @param <LEFT> The left hand side type of the Either result
 * @see Steps for usage examples
 * @see Either
 * @since 4.7.0
 */
public final class EitherStep6<A, B, C, D, E, F, LEFT> {
  private final Either<LEFT, A> either1;
  private final Either<LEFT, B> either2;
  private final Either<LEFT, C> either3;
  private final Either<LEFT, D> either4;
  private final Either<LEFT, E> either5;
  private final Either<LEFT, F> either6;

  EitherStep6(Either<LEFT, A> either1, Either<LEFT, B> either2, Either<LEFT, C> either3, Either<LEFT, D> either4, Either<LEFT, E> either5,
    Either<LEFT, F> either6) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
    this.either4 = either4;
    this.either5 = either5;
    this.either6 = either6;
  }

  /**
   * Apply the provided predicate with the previous step results.
   * <p>
   * If the predicate is not satisfied then the unsatisfiedSupplier is used to
   * populate the left value that will prevent any further steps evaluation.
   *
   * @param predicate The check that must be satisfied by contained values
   * @param unsatisfiedSupplier Provide the value to populate the left if not
   * satisfied
   * @return This step class with either the same last step value, or changed to
   * a left
   */
  public EitherStep6<A, B, C, D, E, F, LEFT> filter(Predicate6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> predicate,
    Supplier<? extends LEFT> unsatisfiedSupplier) {
    Either<LEFT, F> filterEither6 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> either5
      .flatMap(value5 -> either6.filterOrElse(value6 -> predicate.test(value1, value2, value3, value4, value5, value6), unsatisfiedSupplier))))));
    return new EitherStep6<>(either1, either2, either3, either4, either5, filterEither6);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>Right</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The right hand side type for the returned result
   * @return An Either with the result of this function on right, or the
   * existing left
   */
  public <Z> Either<LEFT, Z> yield(Function6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, Z> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> either5.flatMap(value5 -> either6
      .map(value6 -> functor.apply(value1, value2, value3, value4, value5, value6)))))));
  }

}