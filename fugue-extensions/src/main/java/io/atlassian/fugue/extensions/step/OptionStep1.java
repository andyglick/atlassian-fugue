package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The first step of the {@link Option} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Option)}
 *
 * @param <A> The type of the first defined value
 * @see Steps for usage examples
 * @see Option
 * @since 4.7.0
 */
public final class OptionStep1<A> {

  private final Option<A> option1;

  OptionStep1(Option<A> option1) {
    this.option1 = option1;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Option#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * step
   * @param <B> The right hand side type of the next step result
   * @return The next step class
   */
  public <B> OptionStep2<A, B> then(Function<? super A, ? extends Option<? extends B>> functor) {
    Option<B> option2 = option1.flatMap(functor);
    return new OptionStep2<>(option1, option2);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Option#flatMap(Function)} and the
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
  public <B> OptionStep2<A, B> then(Supplier<? extends Option<? extends B>> supplier) {
    Option<B> option2 = option1.flatMap(value1 -> supplier.get());
    return new OptionStep2<>(option1, option2);
  }

  /**
   * Apply the provided predicate with the previous step results.
   *
   * @param predicate The check that must be satisfied by contained values
   * @return This step class with either the same last step value, or changed to
   * none
   */
  public OptionStep1<A> filter(Predicate<? super A> predicate) {
    Option<A> filterOption1 = option1.filter(predicate);
    return new OptionStep1<>(filterOption1);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>some</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return An Option containing this result or none
   */
  public <Z> Option<Z> yield(Function<? super A, Z> functor) {
    return option1.map(functor);
  }

}
