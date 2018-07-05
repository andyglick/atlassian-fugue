package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;
import io.atlassian.fugue.extensions.functions.Function4;
import io.atlassian.fugue.extensions.functions.Predicate4;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The fourth step of the {@link Try} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Try)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @param <C> The type of the third defined value
 * @param <D> The type of the fourth defined value
 * @see Steps for usage examples
 * @see Try
 * @since 4.7.0
 */
public final class TryStep4<A, B, C, D> {

  private final Try<A> try1;
  private final Try<B> try2;
  private final Try<C> try3;
  private final Try<D> try4;

  TryStep4(Try<A> try1, Try<B> try2, Try<C> try3, Try<D> try4) {
    this.try1 = try1;
    this.try2 = try2;
    this.try3 = try3;
    this.try4 = try4;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the result
   * will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <E> The type of the next step result
   * @return The next step class
   */
  public <E> TryStep5<A, B, C, D, E> then(Function4<? super A, ? super B, ? super C, ? super D, Try<E>> functor) {
    Try<E> try5 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> functor.apply(value1, value2, value3,
      value4)))));
    return new TryStep5<>(try1, try2, try3, try4, try5);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the
   * supplier will become the next step value.
   * <p>
   * This is different to {@link #then(Function4)} in that the previous step
   * results are not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the
   * previous step.
   * @param <E> The type of the next step result
   * @return The next step class
   */
  public <E> TryStep5<A, B, C, D, E> then(Supplier<Try<E>> supplier) {
    Try<E> try5 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> supplier.get()))));
    return new TryStep5<>(try1, try2, try3, try4, try5);
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
  public TryStep4<A, B, C, D> filter(Predicate4<? super A, ? super B, ? super C, ? super D> predicate, Supplier<Exception> unsatisfiedSupplier) {
    Try<D> filterTry4 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.filterOrElse(
      value4 -> predicate.test(value1, value2, value3, value4), unsatisfiedSupplier))));
    return new TryStep4<>(try1, try2, try3, filterTry4);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>Success</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return A Try containing this result as success or failure
   */
  public <Z> Try<Z> yield(Function4<? super A, ? super B, ? super C, ? super D, Z> functor) {
    return try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.map(value4 -> functor.apply(value1, value2, value3, value4)))));
  }

}
