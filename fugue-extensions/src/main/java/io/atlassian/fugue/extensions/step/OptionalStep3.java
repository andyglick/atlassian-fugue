package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.extensions.functions.Predicate3;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The third step of the {@link Optional} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Optional)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @param <C> The type of the third defined value
 * @see Steps for usage examples
 * @see Optional
 * @since 4.7.0
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public final class OptionalStep3<A, B, C> {

  private final Optional<A> optional1;
  private final Optional<B> optional2;
  private final Optional<C> optional3;

  OptionalStep3(Optional<A> optional1, Optional<B> optional2, Optional<C> optional3) {
    this.optional1 = optional1;
    this.optional2 = optional2;
    this.optional3 = optional3;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Optional#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <D> The type of the next step result
   * @return The next step class
   */
  public <D> OptionalStep4<A, B, C, D> then(Function3<? super A, ? super B, ? super C, Optional<D>> functor) {
    Optional<D> option4 = optional1
      .flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> functor.apply(value1, value2, value3))));
    return new OptionalStep4<>(optional1, optional2, optional3, option4);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Optional#flatMap(Function)} and the
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
  public <D> OptionalStep4<A, B, C, D> then(Supplier<Optional<D>> supplier) {
    Optional<D> Optional = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> supplier.get())));
    return new OptionalStep4<>(optional1, optional2, optional3, Optional);
  }

  /**
   * Apply the provided predicate with the previous step results.
   *
   * @param predicate The check that must be satisfied by contained values
   * @return This step class with either the same last step value, or changed to
   * empty
   */
  public OptionalStep3<A, B, C> filter(Predicate3<? super A, ? super B, ? super C> predicate) {
    Optional<C> filterOptional3 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.filter(value3 -> predicate.test(value1, value2,
      value3))));
    return new OptionalStep3<>(optional1, optional2, filterOptional3);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>of</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return An Optional containing this result or empty
   */
  public <Z> Optional<Z> yield(Function3<? super A, ? super B, ? super C, Z> functor) {
    return optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.map(value3 -> functor.apply(value1, value2, value3))));
  }

}
