package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.extensions.functions.Function4;
import io.atlassian.fugue.extensions.functions.Predicate4;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The fourth step of the {@link Either} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Either)}
 *
 * @param <A> The right hand side type of the first defined right value
 * @param <B> The right hand side type of the second defined right value
 * @param <C> The right hand side type of the third defined right value
 * @param <D> The right hand side type of the fourth defined right value
 * @param <LEFT> The left hand side type of the Either result
 * @see Steps for usage examples
 * @see Either
 * @since 4.7.0
 */
public final class EitherStep4<A, B, C, D, LEFT> {
  private final Either<LEFT, A> either1;
  private final Either<LEFT, B> either2;
  private final Either<LEFT, C> either3;
  private final Either<LEFT, D> either4;

  EitherStep4(Either<LEFT, A> either1, Either<LEFT, B> either2, Either<LEFT, C> either3, Either<LEFT, D> either4) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
    this.either4 = either4;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Either#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <E> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to
   * {@link LEFT}
   * @return The next step class
   */
  public <E, LL extends LEFT> EitherStep5<A, B, C, D, E, LEFT> then(Function4<? super A, ? super B, ? super C, ? super D, Either<LL, E>> functor) {
    Either<LEFT, E> either5 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> functor.apply(
      value1, value2, value3, value4)))));
    return new EitherStep5<>(either1, either2, either3, either4, either5);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Either#flatMap(Function)} and the
   * supplier will become the next step value.
   * <p>
   * This is different to {@link #then(Function4)} in that the previous step
   * results are not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the
   * previous step.
   * @param <E> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to
   * {@link LEFT}
   * @return The next step class
   */
  public <E, LL extends LEFT> EitherStep5<A, B, C, D, E, LEFT> then(Supplier<Either<LL, E>> supplier) {
    Either<LEFT, E> either5 = either1
      .flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> supplier.get()))));
    return new EitherStep5<>(either1, either2, either3, either4, either5);
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
  public EitherStep4<A, B, C, D, LEFT> filter(Predicate4<? super A, ? super B, ? super C, ? super D> predicate,
    Supplier<? extends LEFT> unsatisfiedSupplier) {
    Either<LEFT, D> filterEither4 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.filterOrElse(
      value4 -> predicate.test(value1, value2, value3, value4), unsatisfiedSupplier))));
    return new EitherStep4<>(either1, either2, either3, filterEither4);
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
  public <Z> Either<LEFT, Z> yield(Function4<? super A, ? super B, ? super C, ? super D, Z> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.map(value4 -> functor.apply(value1, value2, value3,
      value4)))));
  }

}
