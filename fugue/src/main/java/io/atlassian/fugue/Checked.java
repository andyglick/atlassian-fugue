package io.atlassian.fugue;

/**
 * Helpers to work with functions that may throw exceptions. Used with
 * {@link Try}.
 *
 * @since 4.4.0
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) public class Checked {
  // do not ctor
  private Checked() {}

  /**
   * Represents a {@link Function} that may throw an exception.
   *
   * @param <A> the type of the input to the function
   * @param <B> the type of the result of the function
   * @param <E> The type of exception potentially thrown
   */
  @SuppressWarnings("unused") @FunctionalInterface public interface Function<A, B, E extends Exception> {
    B apply(A t) throws E;

    default java.util.function.Function<A, Try<B>> lift() {
      return Checked.lift(this);
    }
  }

  /**
   * Represents a {@link Supplier} that may throw an exception.
   *
   * @param <A> the type of the result of the supplier
   * @param <E> The type of exception potentially thrown
   */
  @SuppressWarnings("unused") @FunctionalInterface public interface Supplier<A, E extends Exception> {
    A get() throws E;

    default Try<A> attempt() {
      return now(this);
    }
  }

  /**
   * Lifts a function that potentially throws into a function that either
   * returns a Success of the value or a failure containing the thrown
   * exception.
   *
   * @param f a function that can throw
   * @param <A> the function argument type
   * @param <B> the function return type
   * @param <E> The type of exception potentially thrown
   * @return the argument function lifted into returning a Try
   *
   */
  public static <A, B, E extends Exception> java.util.function.Function<A, Try<B>> lift(Checked.Function<A, B, E> f) {
    return a -> Checked.now(() -> f.apply(a));
  }

  /**
   * Lifts a function that potentially throws into a function that either
   * returns a delayed Try, which, when evaluated, returns a Success of the
   * result or a failure containing the thrown exception.
   *
   * @param f a function that can throw
   * @param <A> the function argument type
   * @param <B> the function return type
   * @param <E> The type of exception potentially thrown
   * @return the argument function lifted into returning a delayed Try
   *
   */
  public static <A, B, E extends Exception> java.util.function.Function<A, Try<B>> delayedLift(Checked.Function<A, B, E> f) {
    return a -> Checked.delay(() -> f.apply(a));
  }

  /**
   * Create a new Try representing the result of a potentially exception
   * throwing operation. If the provided supplier throws an exception this will
   * return a failure wrapping the exception, otherwise a success of the
   * supplied value will be returned.
   *
   * @param s a supplier that may throw an exception
   * @param <A> the type of value s supplies
   * @param <E> The type of exception potentially thrown
   * @return If s throws an exception this will return a failure wrapping the
   * exception, otherwise a success of the supplied value.
   * @deprecated since 4.6 This is being replaced with {@link #now(Supplier)} to
   * make it clear that the supplier is evaluated immediately. If the evaluation
   * needs to be delayed, use {@link #delay(Supplier)}.
   */
  @Deprecated public static <A, E extends Exception> Try<A> of(Checked.Supplier<A, E> s) {
    return now(s);
  }

  /**
   * Create a new Try representing the result of a potentially exception
   * throwing operation. The provided supplier is evaluated immediately. If the
   * provided supplier throws an exception this will return a failure wrapping
   * the exception, otherwise a success of the supplied value will be returned.
   *
   * @param s a supplier that may throw an exception
   * @param <A> the type of value s supplies
   * @param <E> The type of exception potentially thrown
   * @return If s throws an exception this will return a failure wrapping the
   * exception, otherwise a success of the supplied value.
   */
  public static <A, E extends Exception> Try<A> now(Checked.Supplier<A, E> s) {
    try {
      return Try.successful(s.get());
    } catch (final Exception e) {
      return Try.failure(e);
    }
  }

  /**
   * Create a new delayed Try representing a delayed evaluation of a potentially
   * exception throwing operation. The provided supplier is only called when the
   * delayed Try is evaluated. The provided supplier is called only once no
   * matter how many times the returned delayed Try is evaluated. If the
   * provided supplier throws an exception the evaluation of the delayed Try
   * will return a failure wrapping the exception, otherwise a success of the
   * supplied value will be returned.
   *
   * @param s a supplier that may throw an exception
   * @param <A> the type of value s supplies
   * @param <E> The type of exception potentially thrown
   * @return a delayed Try. If s throws an exception this, when evaluated, will
   * return a failure wrapping the exception, otherwise a success of the
   * supplied value.
   */
  public static <A, E extends Exception> Try<A> delay(Checked.Supplier<A, E> s) {
    return Try.delayed(() -> now(s));
  }
}
