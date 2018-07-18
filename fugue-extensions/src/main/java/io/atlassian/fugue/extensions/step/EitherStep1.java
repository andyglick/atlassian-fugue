package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The first step of the {@link Either} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Either)}
 *
 * @param <A> The right hand side type of the first defined right value
 * @param <LEFT> The left hand side type of the Either result
 * @see Steps for usage examples
 * @see Either
 * @since 4.7.0
 */
public final class EitherStep1<A, LEFT> {

  private final Either<LEFT, A> either1;

  EitherStep1(Either<LEFT, A> either1) {
    this.either1 = either1;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Either#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * step
   * @param <B> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to
   * {@link LEFT}
   * @return The next step class
   */
  public <B, LL extends LEFT> EitherStep2<A, B, LEFT> then(Function<? super A, Either<LL, B>> functor) {
    Either<LEFT, B> either2 = either1.flatMap(functor);
    return new EitherStep2<>(either1, either2);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Either#flatMap(Function)} and the
   * supplier will become the next step value.
   * <p>
   * This is different to {@link #then(Function)} in that the previous step
   * results are not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the
   * previous step.
   * @param <B> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to
   * {@link LEFT}
   * @return The next step class
   */
  public <B, LL extends LEFT> EitherStep2<A, B, LEFT> then(Supplier<Either<LL, B>> supplier) {
    Either<LEFT, B> either2 = either1.flatMap(value1 -> supplier.get());
    return new EitherStep2<>(either1, either2);
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
  public EitherStep1<A, LEFT> filter(Predicate<? super A> predicate, Supplier<? extends LEFT> unsatisfiedSupplier) {
    Either<LEFT, A> filterEither1 = either1.filterOrElse(predicate, unsatisfiedSupplier);
    return new EitherStep1<>(filterEither1);
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
  public <Z> Either<LEFT, Z> yield(Function<? super A, Z> functor) {
    return either1.map(functor);
  }

}
