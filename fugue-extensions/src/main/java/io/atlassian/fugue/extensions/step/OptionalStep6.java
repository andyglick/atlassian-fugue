package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function6;
import io.atlassian.fugue.extensions.functions.Predicate6;

import java.util.Optional;

/**
 * The sixth step of the {@link Optional} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Optional)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @param <C> The type of the third defined value
 * @param <D> The type of the fourth defined value
 * @param <E> The type of the fifth defined value
 * @param <F> The type of the sixth defined value
 * @see Steps for usage examples
 * @see Optional
 * @since 4.7.0
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public final class OptionalStep6<A, B, C, D, E, F> {
  private final Optional<A> optional1;
  private final Optional<B> optional2;
  private final Optional<C> optional3;
  private final Optional<D> optional4;
  private final Optional<E> optional5;
  private final Optional<F> optional6;

  OptionalStep6(Optional<A> optional1, Optional<B> optional2, Optional<C> optional3, Optional<D> optional4, Optional<E> optional5,
    Optional<F> optional6) {
    this.optional1 = optional1;
    this.optional2 = optional2;
    this.optional3 = optional3;
    this.optional4 = optional4;
    this.optional5 = optional5;
    this.optional6 = optional6;
  }

  /**
   * Apply the provided predicate with the previous step results.
   *
   * @param predicate The check that must be satisfied by contained values
   * @return This step class with either the same last step value, or changed to
   * empty
   */
  public OptionalStep6<A, B, C, D, E, F> filter(Predicate6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> predicate) {
    Optional<F> filterOptional6 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4
      .flatMap(value4 -> optional5.flatMap(value5 -> optional6.filter(value6 -> predicate.test(value1, value2, value3, value4, value5, value6)))))));
    return new OptionalStep6<>(optional1, optional2, optional3, optional4, optional5, filterOptional6);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>of</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return An Optional containing this result or empty
   */
  public <Z> Optional<Z> yield(Function6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, Z> functor) {
    return optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.flatMap(value4 -> optional5
      .flatMap(value5 -> optional6.map(value6 -> functor.apply(value1, value2, value3, value4, value5, value6)))))));
  }
}
