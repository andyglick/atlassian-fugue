package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.extensions.functions.Predicate3;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The third step of the {@link Either} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Either)}
 *
 * @param <A> The right hand side type of the first defined right value
 * @param <B> The right hand side type of the second defined right value
 * @param <C> The right hand side type of the third defined right value
 * @param <LEFT> The left hand side type of the Either result
 * @see Steps for usage examples
 * @see Either
 * @since 4.7.0
 */
public final class EitherStep3<A, B, C, LEFT> {

  private final Either<LEFT, A> either1;
  private final Either<LEFT, B> either2;
  private final Either<LEFT, C> either3;

  EitherStep3(Either<LEFT, A> either1, Either<LEFT, B> either2, Either<LEFT, C> either3) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Either#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <D> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to
   * {@link LEFT}
   * @return The next step class
   */
  public <D, LL extends LEFT> EitherStep4<A, B, C, D, LEFT> then(Function3<? super A, ? super B, ? super C, Either<LL, D>> functor) {
    Either<LEFT, D> either4 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> functor.apply(value1, value2, value3))));
    return new EitherStep4<>(either1, either2, either3, either4);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Either#flatMap(Function)} and the
   * supplier will become the next step value.
   * <p>
   * This is different to {@link #then(Function3)} in that the previous step
   * results are not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the
   * previous step.
   * @param <D> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to
   * {@link LEFT}
   * @return The next step class
   */
  public <D, LL extends LEFT> EitherStep4<A, B, C, D, LEFT> then(Supplier<Either<LL, D>> supplier) {
    Either<LEFT, D> either4 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> supplier.get())));
    return new EitherStep4<>(either1, either2, either3, either4);
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
  public EitherStep3<A, B, C, LEFT> filter(Predicate3<? super A, ? super B, ? super C> predicate, Supplier<? extends LEFT> unsatisfiedSupplier) {
    Either<LEFT, C> filterEither3 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.filterOrElse(
      value3 -> predicate.test(value1, value2, value3), unsatisfiedSupplier)));
    return new EitherStep3<>(either1, either2, filterEither3);
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
  public <Z> Either<LEFT, Z> yield(Function3<? super A, ? super B, ? super C, Z> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.map(value3 -> functor.apply(value1, value2, value3))));
  }

}
