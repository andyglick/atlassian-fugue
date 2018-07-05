package io.atlassian.fugue.extensions.step;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The second step of the {@link Optional} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Optional)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @see Steps for usage examples
 * @see Optional
 * @since 4.7.0
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public final class OptionalStep2<A, B> {

  private final Optional<A> optional1;
  private final Optional<B> optional2;

  OptionalStep2(Optional<A> optional1, Optional<B> optional2) {
    this.optional1 = optional1;
    this.optional2 = optional2;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Optional#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <C> The type of the next step result
   * @return The next step class
   */
  public <C> OptionalStep3<A, B, C> then(BiFunction<? super A, ? super B, Optional<C>> functor) {
    Optional<C> option3 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> functor.apply(value1, value2)));
    return new OptionalStep3<>(optional1, optional2, option3);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Optional#flatMap(Function)} and the
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
  public <C> OptionalStep3<A, B, C> then(Supplier<Optional<C>> supplier) {
    Optional<C> Optional = optional1.flatMap(value1 -> optional2.flatMap(value2 -> supplier.get()));
    return new OptionalStep3<>(optional1, optional2, Optional);
  }

  /**
   * Apply the provided predicate with the previous step results.
   *
   * @param predicate The check that must be satisfied by contained values
   * @return This step class with either the same last step value, or changed to
   * empty
   */
  public OptionalStep2<A, B> filter(BiPredicate<? super A, ? super B> predicate) {
    Optional<B> filterOptional2 = optional1.flatMap(value1 -> optional2.filter(value2 -> predicate.test(value1, value2)));
    return new OptionalStep2<>(optional1, filterOptional2);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>of</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return An Optional containing this result or empty
   */
  public <Z> Optional<Z> yield(BiFunction<? super A, ? super B, Z> functor) {
    return optional1.flatMap(value1 -> optional2.map(value2 -> functor.apply(value1, value2)));
  }

}
