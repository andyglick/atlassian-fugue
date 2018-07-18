package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.extensions.functions.Predicate3;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The third step of the {@link Option} type.
 * <p>
 * This class is not intended to be contructed manually, and should only be used
 * as part of a {@link Steps} chain, started by {@link Steps#begin(Option)}
 *
 * @param <A> The type of the first defined value
 * @param <B> The type of the second defined value
 * @param <C> The type of the third defined value
 * @see Steps for usage examples
 * @see Option
 * @since 4.7.0
 */
public final class OptionStep3<A, B, C> {

  private final Option<A> option1;
  private final Option<B> option2;
  private final Option<C> option3;

  OptionStep3(Option<A> option1, Option<B> option2, Option<C> option3) {
    this.option1 = option1;
    this.option2 = option2;
    this.option3 = option3;
  }

  /**
   * Apply the provided function with the previous Step results.
   * <p>
   * Internally this will perform a {@link Option#flatMap(Function)} and the
   * result will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous
   * steps
   * @param <D> The type of the next step result
   * @return The next step class
   */
  public <D> OptionStep4<A, B, C, D> then(Function3<? super A, ? super B, ? super C, ? extends Option<? extends D>> functor) {
    Option<D> option4 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> functor.apply(value1, value2, value3))));
    return new OptionStep4<>(option1, option2, option3, option4);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   * <p>
   * Internally this will perform a {@link Option#flatMap(Function)} and the
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
  public <D> OptionStep4<A, B, C, D> then(Supplier<? extends Option<? extends D>> supplier) {
    Option<D> option4 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> supplier.get())));
    return new OptionStep4<>(option1, option2, option3, option4);
  }

  /**
   * Apply the provided predicate with the previous step results.
   *
   * @param predicate The check that must be satisfied by contained values
   * @return This step class with either the same last step value, or changed to
   * none
   */
  public OptionStep3<A, B, C> filter(Predicate3<? super A, ? super B, ? super C> predicate) {
    Option<C> filterOption3 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.filter(value3 -> predicate.test(value1, value2, value3))));
    return new OptionStep3<>(option1, option2, filterOption3);
  }

  /**
   * Terminating step expression, that will provide the previous steps to this
   * function and return the result as a <code>some</code>
   *
   * @param functor The yield function to map on previous values
   * @param <Z> The type for the returned result
   * @return An Option containing this result or none
   */
  public <Z> Option<Z> yield(Function3<? super A, ? super B, ? super C, Z> functor) {
    return option1.flatMap(value1 -> option2.flatMap(value2 -> option3.map(value3 -> functor.apply(value1, value2, value3))));
  }

}
