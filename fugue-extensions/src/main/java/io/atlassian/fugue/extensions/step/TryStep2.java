package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The second step of the {@link Try} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Try)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @see Steps for usage examples
 * @see Try
 * @since 4.7.0
 */
public final class TryStep2<A, B> {

  private final Try<A> try1;
  private final Try<B> try2;

  TryStep2(Try<A> try1, Try<B> try2) {
    this.try1 = try1;
    this.try2 = try2;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the result
   * will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <C> The type of the next step result
   * @return The next step class
   */
  public <C> TryStep3<A, B, C> then(BiFunction<? super A, ? super B, Try<C>> functor) {
    Try<C> try3 = try1.flatMap(value1 -> try2.flatMap(value2 -> functor.apply(value1, value2)));
    return new TryStep3<>(try1, try2, try3);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the
   * supplier will become the next step value.
   * <p>
   * This is different to {@link #then(BiFunction)} in that the previous step
   * results are not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the
   * previous step.
   * @param <C> The type of the next step result
   * @return The next step class
   */
  public <C> TryStep3<A, B, C> then(Supplier<Try<C>> supplier) {
    Try<C> Try = try1.flatMap(value1 -> try2.flatMap(value2 -> supplier.get()));
    return new TryStep3<>(try1, try2, Try);
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
  public TryStep2<A, B> filter(BiPredicate<? super A, ? super B> predicate, Supplier<Exception> unsatisfiedSupplier) {
    Try<B> filterTry2 = try1.flatMap(value1 -> try2.filterOrElse(value2 -> predicate.test(value1, value2), unsatisfiedSupplier));
    return new TryStep2<>(try1, filterTry2);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>Success</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return A Try containing this result as success or failure
   */
  public <Z> Try<Z> yield(BiFunction<? super A, ? super B, Z> functor) {
    return try1.flatMap(value1 -> try2.map(value2 -> functor.apply(value1, value2)));
  }

}
