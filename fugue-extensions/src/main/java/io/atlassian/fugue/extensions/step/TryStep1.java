package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The first step of the {@link Try} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Try)}
 *
 * @param <A> The type of the first defined value
 * @see Steps for usage examples
 * @see Try
 * @since 4.7.0
 */
public final class TryStep1<A> {

  private final Try<A> try1;

  TryStep1(Try<A> try1) {
    this.try1 = try1;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the result
   * will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * step
   * @param <B> The right hand side type of the next step result
   * @return The next step class
   */
  public <B> TryStep2<A, B> then(Function<? super A, Try<B>> functor) {
    Try<B> try2 = try1.flatMap(functor);
    return new TryStep2<>(try1, try2);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Try#flatMap(Function)} and the
   * supplier will become the next step value.
   * <p>
   * This is different to {@link #then(Function)} in that the previous step
   * results are not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the
   * previous step.
   * @param <B> The type of the next step result
   * @return The next step class
   */
  public <B> TryStep2<A, B> then(Supplier<Try<B>> supplier) {
    Try<B> either2 = try1.flatMap(value1 -> supplier.get());
    return new TryStep2<>(try1, either2);
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
  public TryStep1<A> filter(Predicate<? super A> predicate, Supplier<Exception> unsatisfiedSupplier) {
    Try<A> filterTry1 = try1.filterOrElse(predicate, unsatisfiedSupplier);
    return new TryStep1<>(filterTry1);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>Success</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return A Try containing this result as success or failure
   */
  public <Z> Try<Z> yield(Function<? super A, Z> functor) {
    return try1.map(functor);
  }

}
