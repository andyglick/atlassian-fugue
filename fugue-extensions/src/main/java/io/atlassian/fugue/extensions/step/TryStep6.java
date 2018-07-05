package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;
import io.atlassian.fugue.extensions.functions.Function6;
import io.atlassian.fugue.extensions.functions.Predicate6;

import java.util.function.Supplier;

/**
 * The sixth step of the {@link Try} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Try)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @param <C> The type of the third defined value
 * @param <D> The type of the fourth defined value
 * @param <E> The type of the fifth defined value
 * @param <E> The type of the sixth defined value
 * @see Steps for usage examples
 * @see Try
 * @since 4.7.0
 */
public final class TryStep6<A, B, C, D, E, F> {
  private final Try<A> try1;
  private final Try<B> try2;
  private final Try<C> try3;
  private final Try<D> try4;
  private final Try<E> try5;
  private final Try<F> try6;

  TryStep6(Try<A> try1, Try<B> try2, Try<C> try3, Try<D> try4, Try<E> try5, Try<F> try6) {
    this.try1 = try1;
    this.try2 = try2;
    this.try3 = try3;
    this.try4 = try4;
    this.try5 = try5;
    this.try6 = try6;
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
  public TryStep6<A, B, C, D, E, F> filter(Predicate6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> predicate,
    Supplier<Exception> unsatisfiedSupplier) {
    Try<F> filterTry6 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.flatMap(value5 -> try6
      .filterOrElse(value6 -> predicate.test(value1, value2, value3, value4, value5, value6), unsatisfiedSupplier))))));
    return new TryStep6<>(try1, try2, try3, try4, try5, filterTry6);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>Success</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return A Try containing this result as success or failure
   */
  public <Z> Try<Z> yield(Function6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, Z> functor) {
    return try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.flatMap(value5 -> try6
      .map(value6 -> functor.apply(value1, value2, value3, value4, value5, value6)))))));
  }
}
