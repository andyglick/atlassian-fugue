package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Try;

import java.util.Optional;

/**
 * This provides an entry point for a "for-comprehension" styled syntax shortcut
 * of fugue {@link Option}, {@link Either}, {@link Try} and Java 8
 * {@link Optional}.
 * <p>
 * The intention of this comprehension is to provide access to flatMap, map and
 * filter of these <i>monad</i> types in a way that is easier to read and reason
 * about.
 * <p>
 * A short explanation is:
 *
 * <ul>
 * <li>Each step must preserve the shape of the original monad. For example if
 * you begin with an {@link Option} then each step must return an {@link Option}
 * with the final yield producing an {@link Option} result.</li>
 * <li>Each step retains access to the previously defined/success values</li>
 * <li>On the first empty/failure step result, no further steps will be
 * evaluated</li>
 * <li>Filter provides a convenience method to break out of further evaluation</li>
 * </ul>
 * <p>
 * Simple example usage of Steps to produce an {@link Option} by chaining
 * functions that all have access to the previous values.
 * <p>
 * {@code
 * Option<Integer> result = Steps.begin(some(2))
 * .then(number -> some(number + 3))
 * .then((number1, number2) -> some(number1 * number2))
 * .yield((number1, number2, number3) -> number3 * 4);
 * }
 * <p>
 * This yield will produce a {@link io.atlassian.fugue.Option.Some} containing
 * the final value of 40.
 * <p>
 * This is calculated by:
 *
 * <ol>
 * <li>The initial defined state of the option is 2</li>
 * <li>This value of 2 is given parameter name of number for the first step, and
 * is added to 3, producing 5</li>
 * <li>The next step accepts both parameters (2 and 5) and multiplies them
 * together, and returns this value of 10</li>
 * <li>The final yield accepts all 3 parameters (2, 5, 10) but only cares about
 * the 3rd parameter (10) and multiplies this by 4 to produce 40</li>
 * <li>The result option is now a <code>some</code> of 40</li>
 * </ol>
 * <p>
 * An example of a Steps causing it to break out early, and do no further
 * evaluation is:
 * <p>
 * {@code
 * Option<Integer> result = Steps.begin(some(2))
 * .then(number -> none(Integer.class))
 * .then((number1, number2) -> some(number1 * number2))
 * .yield((number1, number2, number3) -> number3 * 4);
 * }
 * <p>
 * In this example, the result will be <code>none</code> and the functions after
 * the empty state is returned will not be evaluated.
 * <p>
 * While each step provides access to the previous values, if these are not
 * required, then it is possible to evaluate a new value using a Supplier at
 * each step. For example, the following is possible:
 * <p>
 * {@code
 * Option<Integer> result = Steps.begin(some(2))
 * .then(() -> some(5))
 * .then((number1, number2) -> some(number1 * number2))
 * .yield((number1, number2, number3) -> number3 * 4);
 * }
 * <p>
 * In this case we have decided that the first step will evaluate its result
 * without requiring the previous state, and just return 5. This final result
 * will still be a <code>some</code> of 40.
 * <p>
 * Filter is another method to break out of further evaulations and return the
 * empty result. The following example, illustrates this:
 * <p>
 * {@code
 * Option<Integer> result = Steps.begin(some(2))
 * .then(number -> some(number + 3))
 * .filter((number1, number2) -> number2 == 0)
 * .then((number1, number2) -> some(number1 * number2))
 * .yield((number1, number2, number3) -> number3 * 4);
 * }
 * <p>
 * In this contrived example, the filter predicate will return false as number2
 * will always be 5, and never 0. This will result in that steps result being
 * changed to the empty value, and prevent any further step evaluation.
 * <p>
 * NB: It is possible to chain together up to 6 steps
 *
 * @see Option
 * @see Either
 * @see Try
 * @see Optional
 * @since 4.7.0
 */
public final class Steps {

  private Steps() {
    // do not instantiate
  }

  /**
   * Begin a new Steps expresison, using the {@link Either} type.
   * <p>
   * This assumes a Right-Biased Either, and will therefore continue evaluating
   * steps on <code>right</code> results, and break out early on the first
   * <code>left</code> result.
   *
   * @param either The start value for this steps.
   * @param <A> The right hand side type for the initial {@link Either}
   * @param <LEFT> The left hand side type for the initial {@link Either}
   * @return A class allowing continuation of this first Step
   * @see Either
   * @see Steps for description of Steps usage with examples
   */
  public static <A, LEFT> EitherStep1<A, LEFT> begin(Either<LEFT, A> either) {
    return new EitherStep1<>(either);
  }

  /**
   * Begin a new Steps expresison, using the {@link Option} type.
   * <p>
   * This will continue evaluating steps on <code>some</code> results, and break
   * out early on the first <code>none</code> result.
   *
   * @param option The start value for this steps.
   * @param <A> The type for the initial {@link Option}
   * @return A class allowing continuation of this first Step
   * @see Option
   * @see Steps for description of Steps usage with examples
   */
  public static <A> OptionStep1<A> begin(Option<A> option) {
    return new OptionStep1<>(option);
  }

  /**
   * Begin a new Steps expresison, using the {@link Optional} type.
   * <p>
   * This will continue evaluating steps on <code>of</code> results, and break
   * out early on the first <code>empty</code> result.
   *
   * @param optional The start value for this steps.
   * @param <A> The type for the initial {@link Optional}
   * @return A class allowing continuation of this first Step
   * @see Optional
   * @see Steps for description of Steps usage with examples
   */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType") public static <A> OptionalStep1<A> begin(Optional<A> optional) {
    return new OptionalStep1<>(optional);
  }

  /**
   * Begin a new Steps expresison, using the {@link Try} type.
   * <p>
   * This will continue evaluating steps on <code>success</code> results, and
   * break out early on the first <code>failure</code> result.
   *
   * @param aTry The start value for this steps.
   * @param <A> The type for the initial {@link Try}
   * @return A class allowing continuation of this first Step
   * @see Try
   * @see Steps for description of Steps usage with examples
   */
  public static <A> TryStep1<A> begin(Try<A> aTry) {
    return new TryStep1<>(aTry);
  }

}
