package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;
import io.atlassian.fugue.extensions.functions.Function5;
import io.atlassian.fugue.extensions.functions.Predicate5;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The fifth step of the {@link Try} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Try)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @param <C> The type of the third defined value
 * @param <D> The type of the fourth defined value
 * @param <E> The type of the fifth defined value
 * @see Steps for usage examples
 * @see Try
 * @since 4.7.0
 */
public final class TryStep5<A, B, C, D, E> {
  private final Try<A> try1;
  private final Try<B> try2;
  private final Try<C> try3;
  private final Try<D> try4;
  private final Try<E> try5;

  TryStep5(Try<A> try1, Try<B> try2, Try<C> try3, Try<D> try4, Try<E> try5) {
    this.try1 = try1;
    this.try2 = try2;
    this.try3 = try3;
    this.try4 = try4;
    this.try5 = try5;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the result
   * will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <F> The type of the next step result
   * @return The next step class
   */
  public <F> TryStep6<A, B, C, D, E, F> then(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Try<F>> functor) {
    Try<F> try6 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.flatMap(value5 -> functor.apply(
      value1, value2, value3, value4, value5))))));
    return new TryStep6<>(try1, try2, try3, try4, try5, try6);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the
   * supplier will become the next step value.
   * <p>
   * This is different to {@link #then(Function5)} in that the previous step
   * results are not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the
   * previous step.
   * @param <F> The type of the next step result
   * @return The next step class
   */
  public <F> TryStep6<A, B, C, D, E, F> then(Supplier<Try<F>> supplier) {
    Try<F> try6 = try1
      .flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.flatMap(value5 -> supplier.get())))));
    return new TryStep6<>(try1, try2, try3, try4, try5, try6);
  }

  /**
   * Apply the provided predicate with the previous step results.
   * <p>
   * If the predicate is not satisfied then the unsatisfiedSupplier is used to
   * populate the failure value that will prevent any further steps evaluation.
   *
   * @param predicate The check that must be satisfied by contained values
   * @param unsatisfiedSupplier Provide the value to populate the failure if not
   * satisfied
   * @return This step class with either the same last step value, or changed to
   * a failure
   */
  public TryStep5<A, B, C, D, E> filter(Predicate5<? super A, ? super B, ? super C, ? super D, ? super E> predicate,
    Supplier<Exception> unsatisfiedSupplier) {
    Try<E> filterTry5 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.filterOrElse(
      value5 -> predicate.test(value1, value2, value3, value4, value5), unsatisfiedSupplier)))));
    return new TryStep5<>(try1, try2, try3, try4, filterTry5);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>Success</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return A Try containing this result as success or failure
   */
  public <Z> Try<Z> yield(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Z> functor) {
    return try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.map(value5 -> functor.apply(value1,
      value2, value3, value4, value5))))));
  }
}
