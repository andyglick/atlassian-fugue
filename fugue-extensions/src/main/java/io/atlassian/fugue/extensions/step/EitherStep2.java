package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The second step of the {@link Either} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Either)}
 *
 * @param <A> The right hand side type of the first defined right value
 * @param <B> The right hand side type of the second defined right value
 * @param <LEFT> The left hand side type of the Either result
 * @see Steps for usage examples
 * @see Either
 * @since 4.7.0
 */
public final class EitherStep2<A, B, LEFT> {

  private final Either<LEFT, A> either1;
  private final Either<LEFT, B> either2;

  EitherStep2(Either<LEFT, A> either1, Either<LEFT, B> either2) {
    this.either1 = either1;
    this.either2 = either2;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Either#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <C> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to
   * {@link LEFT}
   * @return The next step class
   */
  public <C, LL extends LEFT> EitherStep3<A, B, C, LEFT> then(BiFunction<? super A, ? super B, Either<LL, C>> functor) {
    Either<LEFT, C> either3 = either1.flatMap(value1 -> either2.flatMap(value2 -> functor.apply(value1, value2)));
    return new EitherStep3<>(either1, either2, either3);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Either#flatMap(Function)} and the
   * supplier will become the next step value.
   * <p>
   * This is different to {@link #then(BiFunction)} in that the previous step
   * results are not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the
   * previous step.
   * @param <C> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to
   * {@link LEFT}
   * @return The next step class
   */
  public <C, LL extends LEFT> EitherStep3<A, B, C, LEFT> then(Supplier<Either<LL, C>> supplier) {
    Either<LEFT, C> either3 = either1.flatMap(value1 -> either2.flatMap(value2 -> supplier.get()));
    return new EitherStep3<>(either1, either2, either3);
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
  public EitherStep2<A, B, LEFT> filter(BiPredicate<? super A, ? super B> predicate, Supplier<? extends LEFT> unsatisfiedSupplier) {
    Either<LEFT, B> filterEither2 = either1.flatMap(value1 -> either2.filterOrElse(value2 -> predicate.test(value1, value2), unsatisfiedSupplier));
    return new EitherStep2<>(either1, filterEither2);
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
  public <Z> Either<LEFT, Z> yield(BiFunction<? super A, ? super B, Z> functor) {
    return either1.flatMap(value1 -> either2.map(value2 -> functor.apply(value1, value2)));
  }

}
