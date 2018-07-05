package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The second step of the {@link Option} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Option)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @see Steps for usage examples
 * @see Option
 * @since 4.7.0
 */
public final class OptionStep2<A, B> {

  private final Option<A> option1;
  private final Option<B> option2;

  OptionStep2(Option<A> option1, Option<B> option2) {
    this.option1 = option1;
    this.option2 = option2;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Option#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <C> The type of the next step result
   * @return The next step class
   */
  public <C> OptionStep3<A, B, C> then(BiFunction<? super A, ? super B, ? extends Option<? extends C>> functor) {
    Option<C> option3 = option1.flatMap(value1 -> option2.flatMap(value2 -> functor.apply(value1, value2)));
    return new OptionStep3<>(option1, option2, option3);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Option#flatMap(Function)} and the
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
  public <C> OptionStep3<A, B, C> then(Supplier<? extends Option<? extends C>> supplier) {
    Option<C> option3 = option1.flatMap(value1 -> option2.flatMap(value2 -> supplier.get()));
    return new OptionStep3<>(option1, option2, option3);
  }

  /**
   * Apply the provided predicate with the previous step results.
   *
   * @param predicate The check that must be satisfied by contained values
   * @return This step class with either the same last step value, or changed to
   * none
   */
  public OptionStep2<A, B> filter(BiPredicate<? super A, ? super B> predicate) {
    Option<B> filterOption2 = option1.flatMap(value1 -> option2.filter(value2 -> predicate.test(value1, value2)));
    return new OptionStep2<>(option1, filterOption2);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>some</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return An Option containing this result or none
   */
  public <Z> Option<Z> yield(BiFunction<? super A, ? super B, Z> functor) {
    return option1.flatMap(value1 -> option2.map(value2 -> functor.apply(value1, value2)));
  }

}
