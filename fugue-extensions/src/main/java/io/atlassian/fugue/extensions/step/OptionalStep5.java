package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function5;
import io.atlassian.fugue.extensions.functions.Predicate5;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The fifth step of the {@link Optional} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Optional)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @param <C> The type of the third defined value
 * @param <D> The type of the fourth defined value
 * @param <E> The type of the fifth defined value
 * @see Steps for usage examples
 * @see Optional
 * @since 4.7.0
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public final class OptionalStep5<A, B, C, D, E> {
  private final Optional<A> optional1;
  private final Optional<B> optional2;
  private final Optional<C> optional3;
  private final Optional<D> optional4;
  private final Optional<E> optional5;

  OptionalStep5(Optional<A> optional1, Optional<B> optional2, Optional<C> optional3, Optional<D> optional4, Optional<E> optional5) {
    this.optional1 = optional1;
    this.optional2 = optional2;
    this.optional3 = optional3;
    this.optional4 = optional4;
    this.optional5 = optional5;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Optional#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <F> The type of the next step result
   * @return The next step class
   */
  public <F> OptionalStep6<A, B, C, D, E, F> then(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Optional<F>> functor) {
    Optional<F> optional6 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.flatMap(value4 -> optional5
      .flatMap(value5 -> functor.apply(value1, value2, value3, value4, value5))))));
    return new OptionalStep6<>(optional1, optional2, optional3, optional4, optional5, optional6);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Optional#flatMap(Function)} and the
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
  public <F> OptionalStep6<A, B, C, D, E, F> then(Supplier<Optional<F>> supplier) {
    Optional<F> optional6 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.flatMap(value4 -> optional5
      .flatMap(value5 -> supplier.get())))));
    return new OptionalStep6<>(optional1, optional2, optional3, optional4, optional5, optional6);
  }

  /**
   * Apply the provided predicate with the previous step results.
   *
   * @param predicate The check that must be satisfied by contained values
   * @return This step class with either the same last step value, or changed to
   * empty
   */
  public OptionalStep5<A, B, C, D, E> filter(Predicate5<? super A, ? super B, ? super C, ? super D, ? super E> predicate) {
    Optional<E> filterOptional5 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4
      .flatMap(value4 -> optional5.filter(value5 -> predicate.test(value1, value2, value3, value4, value5))))));
    return new OptionalStep5<>(optional1, optional2, optional3, optional4, filterOptional5);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>of</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return An Optional containing this result or empty
   */
  public <Z> Optional<Z> yield(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Z> functor) {
    return optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.flatMap(value4 -> optional5
      .map(value5 -> functor.apply(value1, value2, value3, value4, value5))))));
  }
}
