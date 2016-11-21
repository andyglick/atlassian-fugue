package io.atlassian.fugue;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

/**
 * A <code>Try</code> represents a computation that may either throw an
 * exception or return a value. A Try will either be {@link Try.Success Success}
 * wrapping a value or {@link Try.Failure Failure} which wraps an exception.
 * <p>
 * This class is similar to {@link Either}, but is explicit about having a
 * success and failure case. Unless method level javadoc says otherwise, methods
 * will not automatically catch exceptions thrown by function arguments. In
 * particular map will not catch automatically catch thrown exceptions, instead
 * you should use {@link Checked#lift} to to make the function explicitly return
 * a Try and the use flatmap.
 */
public abstract class Try<A> {
  /**
   * Creates a new failure
   *
   * @param e an exception to wrap, must not be null.
   * @param <A> the success type
   * @return a new Failure wrapping e.
   */
  public static <A> Try<A> failure(final Exception e) {
    return new Failure<>(e);
  }

  /**
   * Creates a new Success
   *
   * @param value a value to wrap, must not be null
   * @param <A> the wrapped value type
   * @return a new Success wrapping v
   */
  public static <A> Try<A> successful(final A value) {
    return new Success<>(value);
  }

  /**
   * Returns a success wrapping all of the values if all of the arguments were a
   * success, otherwise this returns the first failure
   *
   * @param trys an iterable of try values
   * @param <A> The success type
   * @return a success wrapping all of the values if all of the arguments were a
   * success, otherwise this returns the first failure
   */
  public static <A> Try<Iterable<A>> sequence(Iterable<Try<A>> trys) {
    final ArrayList<A> ts = new ArrayList<>();
    for (final Try<A> t : trys) {
      if (t.isFailure()) {
        return new Failure<>(t.fold(identity(), x -> {
          throw new NoSuchElementException();
        }));
      }
      ts.add(t.fold(f -> {
        throw new NoSuchElementException();
      }, identity()));
    }
    return new Success<>(unmodifiableList(ts));
  }

  /**
   * Reduces a nested Try by a single level
   *
   * @param t A nested Try
   * @param <A> The success type
   * @return The flattened try
   */
  public static <A> Try<A> flatten(Try<Try<A>> t) {
    return t.flatMap(identity());
  }

  /**
   * Returns <code>true</code> if this failure, otherwise <code>false</code>
   *
   * @return <code>true</code> if this failure, otherwise <code>false</code>
   */
  public abstract boolean isFailure();

  /**
   * Returns <code>true</code> if this success, otherwise <code>false</code>
   *
   * @return <code>true</code> if this success, otherwise <code>false</code>
   */
  public abstract boolean isSuccess();

  /**
   * Binds the given function across the success value if it is one.
   *
   * @param <B> result type
   * @param f the function to bind.
   * @return A new Try value after binding with the function applied if this is
   * a Success, otherwise returns this if this is a `Failure`.
   */
  public abstract <B> Try<B> flatMap(Function<? super A, Try<B>> f);

  /**
   * Maps the given function to the value from this `Success` or returns this
   * unchanged if a `Failure`.
   *
   * @param <B> result type
   * @param f the function to apply
   * @return `f` applied to the `Success`, otherwise returns this if this is a
   * `Failure`.
   */
  public abstract <B> Try<B> map(Function<? super A, ? extends B> f);

  /**
   * Applies the given function `f` if this is a `Failure` otherwise this
   * unchanged if a 'Success'. This is like map for the failure.
   *
   * @param f the function to apply
   * @return `f` applied to the `Failure`, otherwise returns this if this is a
   * `Success`.
   */
  public abstract Try<A> recover(Function<? super Exception, A> f);

  /**
   * Binds the given function across the failure value if it is one, otherwise
   * this unchanged if a 'Success'. This is like flatmap for the failure.
   *
   * @param f the function to bind.
   * @return A new Try value after binding with the function applied if this is
   * a Success, otherwise returns this if this is a `Failure`.
   */
  public abstract Try<A> recoverWith(Function<? super Exception, Try<A>> f);

  /**
   * Returns the contained value if this is a success otherwise call the
   * supplier and return its value.
   *
   * @param s called if this is a failure
   * @return the wrapped value or the value from the {@code Supplier}
   */
  public abstract A getOrElse(Supplier<A> s);

  /**
   * Applies the function to the wrapped value, applying failureF it this is a
   * Left and successF if this is a Right.
   *
   * @param failureF the function to apply if this is a Failure
   * @param successF the function to apply if this is a Success
   * @param <B> the destination type
   * @return the result of the applied function
   */
  public abstract <B> B fold(Function<? super Exception, B> failureF, Function<A, B> successF);

  /**
   * Convert this Try to an {@link Either}, becoming a left if this is a failure
   * and a right if this is a success.
   *
   * @return this value wrapped in right if a success, and the exception wrapped
   * in a left if a failure.
   */
  public abstract Either<Exception, A> toEither();

  /**
   * Convert this Try to an Option. Returns <code>Some</code> with a value if it
   * is a success, otherwise <code>None</code>.
   *
   * @return The success's value in <code>Some</code> if it exists, otherwise
   * <code>None</code>
   */
  public abstract Option<A> toOption();

  private static final class Failure<A> extends Try<A> {

    private final Exception e;

    Failure(final Exception e) {
      this.e = requireNonNull(e);
    }

    @Override public <B> Try<B> map(final Function<? super A, ? extends B> f) {
      return new Failure<>(e);
    }

    @Override public boolean isFailure() {
      return true;
    }

    @Override public boolean isSuccess() {
      return false;
    }

    @Override public <B> Try<B> flatMap(final Function<? super A, Try<B>> f) {
      return Try.failure(e);
    }

    @Override public Try<A> recover(final Function<? super Exception, A> f) {
      return Checked.of(() -> f.apply(e));
    }

    @Override public Try<A> recoverWith(final Function<? super Exception, Try<A>> f) {
      return f.apply(e);
    }

    @Override public A getOrElse(final Supplier<A> s) {
      return s.get();
    }

    @Override public <B> B fold(final Function<? super Exception, B> failureF, final Function<A, B> successF) {
      return failureF.apply(e);
    }

    @Override public Either<Exception, A> toEither() {
      return Either.left(e);
    }

    @Override public Option<A> toOption() {
      return Option.none();
    }

    @Override public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      final Failure<?> failure = (Failure<?>) o;

      return e != null ? e.equals(failure.e) : failure.e == null;
    }

    @Override public int hashCode() {
      return ~e.hashCode();
    }
  }

  private static final class Success<A> extends Try<A> {

    private final A value;

    Success(final A value) {
      this.value = requireNonNull(value);
    }

    @Override public <B> Try<B> map(final Function<? super A, ? extends B> f) {
      return Checked.of(() -> f.apply(value));
    }

    @Override public boolean isFailure() {
      return false;
    }

    @Override public boolean isSuccess() {
      return true;
    }

    @Override public <B> Try<B> flatMap(final Function<? super A, Try<B>> f) {
      return f.apply(value);
    }

    @Override public Try<A> recover(final Function<? super Exception, A> f) {
      return this;
    }

    @Override public Try<A> recoverWith(final Function<? super Exception, Try<A>> f) {
      return this;
    }

    @Override public A getOrElse(final Supplier<A> s) {
      return value;
    }

    @Override public <B> B fold(final Function<? super Exception, B> failureF, final Function<A, B> successF) {
      return successF.apply(value);
    }

    @Override public Either<Exception, A> toEither() {
      return Either.right(value);
    }

    @Override public Option<A> toOption() {
      return Option.some(value);
    }

    @Override public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      final Success<?> success = (Success<?>) o;
      return value != null ? value.equals(success.value) : success.value == null;
    }

    @Override public int hashCode() {
      return value.hashCode();
    }
  }
}
