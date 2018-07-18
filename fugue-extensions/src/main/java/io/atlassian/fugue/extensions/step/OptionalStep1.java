package io.atlassian.fugue.extensions.step;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The first step of the {@link Optional} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Optional)}
 *
 * @param <A> The type of the first defined value
 * @see Steps for usage examples
 * @see Optional
 * @since 4.7.0
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public final class OptionalStep1<A> {

  private final Optional<A> optional1;

  OptionalStep1(Optional<A> optional1) {
    this.optional1 = optional1;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Optional#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * step
   * @param <B> The right hand side type of the next step result
   * @return The next step class
   */
  public <B> OptionalStep2<A, B> then(Function<? super A, Optional<B>> functor) {
    Optional<B> option2 = optional1.flatMap(functor);
    return new OptionalStep2<>(optional1, option2);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Optional#flatMap(Function)} and the
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
  public <B> OptionalStep2<A, B> then(Supplier<Optional<B>> supplier) {
    Optional<B> Optional = optional1.flatMap(value1 -> supplier.get());
    return new OptionalStep2<>(optional1, Optional);
  }

  /**
   * Apply the provided predicate with the previous step results.
   *
   * @param predicate The check that must be satisfied by contained values
   * @return This step class with either the same last step value, or changed to
   * empty
   */
  public OptionalStep1<A> filter(Predicate<? super A> predicate) {
    Optional<A> filterOptional1 = optional1.filter(predicate);
    return new OptionalStep1<>(filterOptional1);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>of</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return An Optional containing this result or empty
   */
  public <Z> Optional<Z> yield(Function<? super A, Z> functor) {
    return optional1.map(functor);
  }

}
