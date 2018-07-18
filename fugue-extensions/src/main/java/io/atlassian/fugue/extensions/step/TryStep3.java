package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;
import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.extensions.functions.Predicate3;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The third step of the {@link Try} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Try)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @param <C> The type of the third defined value
 * @see Steps for usage examples
 * @see Try
 * @since 4.7.0
 */
public final class TryStep3<A, B, C> {

  private final Try<A> try1;
  private final Try<B> try2;
  private final Try<C> try3;

  TryStep3(Try<A> try1, Try<B> try2, Try<C> try3) {
    this.try1 = try1;
    this.try2 = try2;
    this.try3 = try3;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the result
   * will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <D> The type of the next step result
   * @return The next step class
   */
  public <D> TryStep4<A, B, C, D> then(Function3<? super A, ? super B, ? super C, Try<D>> functor) {
    Try<D> try4 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> functor.apply(value1, value2, value3))));
    return new TryStep4<>(try1, try2, try3, try4);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the
   * supplier will become the next step value.
   * <p>
   * This is different to {@link #then(Function3)} in that the previous step
   * results are not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the
   * previous step.
   * @param <D> The type of the next step result
   * @return The next step class
   */
  public <D> TryStep4<A, B, C, D> then(Supplier<Try<D>> supplier) {
    Try<D> try4 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> supplier.get())));
    return new TryStep4<>(try1, try2, try3, try4);
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
  public TryStep3<A, B, C> filter(Predicate3<? super A, ? super B, ? super C> predicate, Supplier<Exception> unsatisfiedSupplier) {
    Try<C> filterTry3 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.filterOrElse(value3 -> predicate.test(value1, value2, value3),
      unsatisfiedSupplier)));
    return new TryStep3<>(try1, try2, filterTry3);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>Success</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return A Try containing this result as success or failure
   */
  public <Z> Try<Z> yield(Function3<? super A, ? super B, ? super C, Z> functor) {
    return try1.flatMap(value1 -> try2.flatMap(value2 -> try3.map(value3 -> functor.apply(value1, value2, value3))));
  }

}
