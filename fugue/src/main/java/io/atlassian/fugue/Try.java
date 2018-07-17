package io.atlassian.fugue;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.atlassian.fugue.Either.left;
import static io.atlassian.fugue.Either.right;
import static io.atlassian.fugue.Option.none;
import static io.atlassian.fugue.Option.some;
import static io.atlassian.fugue.Suppliers.memoize;
import static io.atlassian.fugue.Suppliers.ofInstance;
import static io.atlassian.fugue.Unit.Unit;
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
 * particular {@link #map(Function)} will not catch automatically catch thrown
 * exceptions, instead you should use {@link Checked#lift} to to make the
 * function explicitly return a Try and the use {@link #flatMap(Function)}.
 * <p>
 * Note that since 4.7.0, all API methods describe whether or not they will
 * result in a {@link Try.Delayed Delayed} Try being evaluated. In addition to
 * this, any action to {@link Serializable Serialize} a {@link Try.Delayed
 * Delayed} Try will result in it being evaluated, and the underlying Try
 * {@link Try.Success Success} or {@link Try.Failure Failure} result being
 * serialized.
 *
 * @since 4.4.0
 */
@SuppressWarnings("WeakerAccess") public abstract class Try<A> implements Serializable {
  private static final long serialVersionUID = -999421999482330308L;

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
   * Creates a delayed Try, which will return either a Failure or a Success when
   * evaluated. The supplier is called only once, no matter how many times the
   * returned delayed Try is evaluated.
   *
   * @param supplier a supplier that returns a Try of A.
   * @param <A> the wrapped value type
   * @return a new Delayed Try wrapping the supplier.
   */
  public static <A> Try<A> delayed(final Supplier<Try<A>> supplier) {
    return Delayed.fromSupplier(supplier);
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
  public static <A> Try<Iterable<A>> sequence(final Iterable<Try<A>> trys) {
    return sequence(trys, Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Returns a success wrapping all of the values if all of the arguments were a
   * success, otherwise this returns the first failure
   *
   * @param trys an iterable of try values
   * @param collector result collector
   * @param <T> The success type
   * @param <A> The intermediate accumulator type
   * @param <R> The result type
   * @return a success wrapping all of the values if all of the arguments were a
   * success, otherwise this returns the first failure
   * @since 4.6.0
   */
  public static <T, A, R> Try<R> sequence(final Iterable<Try<T>> trys, final Collector<T, A, R> collector) {
    A accumulator = collector.supplier().get();
    for (final Try<T> t : trys) {
      if (t.isFailure()) {
        return Try.failure(t.fold(identity(), x -> {
          throw new NoSuchElementException();
        }));
      }
      collector.accumulator().accept(accumulator, t.fold(f -> {
        throw new NoSuchElementException();
      }, identity()));
    }
    return Try.successful(collector.finisher().apply(accumulator));
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
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this <strong>is</strong> an
   * evaluating operation.
   *
   * @return <code>true</code> if this failure, otherwise <code>false</code>
   */
  public abstract boolean isFailure();

  /**
   * Returns <code>true</code> if this success, otherwise <code>false</code>
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this <strong>is</strong> an
   * evaluating operation.
   *
   * @return <code>true</code> if this success, otherwise <code>false</code>
   */
  public abstract boolean isSuccess();

  /**
   * Binds the given function across the success value if it is one.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this is not an evaluating
   * operation.
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
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this is not an evaluating
   * operation.
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
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this is not an evaluating
   * operation.
   *
   * @param f the function to apply
   * @return `f` applied to the `Failure`, otherwise returns this if this is a
   * `Success`.
   */
  public abstract Try<A> recover(Function<? super Exception, A> f);

  /**
   * Applies the given function `f` if this is a `Failure` with certain
   * exception type otherwise leaves this unchanged. This is like map for
   * exceptions types.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this is not an evaluating
   * operation.
   *
   * @param exceptionType exception class
   * @param f the function to apply
   * @param <X> exception type
   * @return `f` applied to the `Failure`, otherwise returns this if this is a
   * `Success` or the exception does not match the exception type.
   */
  public abstract <X extends Exception> Try<A> recover(Class<X> exceptionType, Function<? super X, A> f);

  /**
   * Binds the given function across the failure value if it is one, otherwise
   * this unchanged if a 'Success'. This is like flatmap for the failure.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this is not an evaluating
   * operation.
   *
   * @param f the function to bind.
   * @return A new Try value after binding with the function applied if this is
   * a `Failure`, otherwise returns this if this is a `Success`.
   */
  public abstract Try<A> recoverWith(Function<? super Exception, Try<A>> f);

  /**
   * Binds the given function across certain exception type if it is one,
   * otherwise this unchanged. This is like flatmap for exceptions types.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this is not an evaluating
   * operation.
   *
   * @param exceptionType exception class
   * @param f the function to apply
   * @param <X> exception type
   * @return A new Try value after binding with the function applied if this is
   * a `Failure`, otherwise returns this if this is a `Success` or the exception
   * does not match the exception type.
   */
  public abstract <X extends Exception> Try<A> recoverWith(Class<X> exceptionType, Function<? super X, Try<A>> f);

  /**
   * Returns the contained value if this is a success otherwise call the
   * supplier and return its value.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this <strong>is</strong> an
   * evaluating operation.
   *
   * @param s called if this is a failure
   * @return the wrapped value or the value from the {@code Supplier}
   */
  public abstract A getOrElse(Supplier<A> s);

  /**
   * If this is a success, return the same success. Otherwise, return
   * {@code orElse}.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this is not an evaluating
   * operation.
   *
   * @param orElse try to return if this is failure
   * @return this or {@code orElse}
   * @since 4.7
   */
  public final Try<A> orElse(final Try<? extends A> orElse) {
    return this.orElse(ofInstance(orElse));
  }

  /**
   * If this is a success, return the same success. Otherwise, return value
   * supplied by {@code orElse}.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this is not an evaluating
   * operation.
   *
   * @param orElse try to return if this is failure
   * @return this or {@code orElse}
   * @since 4.7
   */
  public abstract Try<A> orElse(final Supplier<? extends Try<? extends A>> orElse);

  /**
   * Return a <code>Success</code> if this is a <code>Success</code> and the
   * contained values satisfies the given predicate.
   *
   * If this is a <code>Success</code> but the predicate is not satisfied,
   * return a <code>Failure</code> with the value provided by the
   * orElseSupplier.
   *
   * Return a <code>Failure</code> if this a <code>Failure</code> with the
   * contained value.
   *
   * @param p The predicate function to test on the right contained value.
   * @param orElseSupplier The supplier to execute when is a success, and
   * predicate is unsatisfied
   * @return a new Try that will be either the existing success/failure or a
   * failure with result of orElseSupplier
   * @since 4.7.0
   */
  public abstract Try<A> filterOrElse(Predicate<? super A> p, Supplier<Exception> orElseSupplier);

  /**
   * Applies the function to the wrapped value, applying failureF it this is a
   * Failure and successF if this is a Success.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this <strong>is</strong> an
   * evaluating operation.
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
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this <strong>is</strong> an
   * evaluating operation.
   *
   * @return this value wrapped in right if a success, and the exception wrapped
   * in a left if a failure.
   */
  public abstract Either<Exception, A> toEither();

  /**
   * Convert this Try to an Option. Returns <code>Some</code> with a value if it
   * is a success, otherwise <code>None</code>.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this <strong>is</strong> an
   * evaluating operation.
   *
   * @return The success's value in <code>Some</code> if it exists, otherwise
   * <code>None</code>
   */
  public abstract Option<A> toOption();

  /**
   * Create a {@link java.util.Optional} from this try.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this <strong>is</strong> an
   * evaluating operation.
   *
   * @return {@link java.util.Optional#of(Object)} with the value if success,
   * {@link java.util.Optional#empty()} if failure.
   * @since 4.7
   */
  public abstract Optional<A> toOptional();

  /**
   * Create a {@link java.util.stream.Stream} from this try.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this <strong>is</strong> an
   * evaluating operation.
   *
   * @return {@link java.util.stream.Stream#of(Object)} with the value if
   * success, {@link java.util.stream.Stream#empty()} if failure.
   * @since 4.7
   */
  public abstract Stream<A> toStream();

  /**
   * Perform the given {@link java.util.function.Consumer} (side-effect) for the
   * success {@link #isSuccess() if success} value.
   * <p>
   * Note that for {@link Try#delayed(Supplier)} this <strong>is</strong> an
   * evaluating operation.
   *
   * @param action the {@link java.util.function.Consumer} to apply on the
   * success value
   * @since 4.7
   */
  public abstract void forEach(Consumer<? super A> action);

  private static final class Failure<A> extends Try<A> {
    private static final long serialVersionUID = 735762069058538901L;

    private final Exception e;

    Failure(final Exception e) {
      this.e = requireNonNull(e);
    }

    @Override public <B> Try<B> map(final Function<? super A, ? extends B> f) {
      return Try.failure(e);
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
      return Checked.now(() -> f.apply(e));
    }

    @SuppressWarnings("unchecked") @Override public <X extends Exception> Try<A> recover(final Class<X> exceptionType, final Function<? super X, A> f) {
      return exceptionType.isAssignableFrom(e.getClass()) ? Checked.now(() -> f.apply((X) e)) : this;
    }

    @Override public Try<A> recoverWith(final Function<? super Exception, Try<A>> f) {
      return f.apply(e);
    }

    @SuppressWarnings("unchecked") @Override public <X extends Exception> Try<A> recoverWith(Class<X> exceptionType, Function<? super X, Try<A>> f) {
      return exceptionType.isAssignableFrom(e.getClass()) ? f.apply((X) e) : this;
    }

    @Override public A getOrElse(final Supplier<A> s) {
      return s.get();
    }

    @Override public Try<A> orElse(Supplier<? extends Try<? extends A>> orElse) {
      @SuppressWarnings("unchecked")
      Try<A> result = (Try<A>) orElse.get();
      return result;
    }

    @Override public Try<A> filterOrElse(Predicate<? super A> p, Supplier<Exception> orElseSupplier) {
      return Try.failure(e);
    }

    @Override public <B> B fold(final Function<? super Exception, B> failureF, final Function<A, B> successF) {
      return failureF.apply(e);
    }

    @Override public Either<Exception, A> toEither() {
      return left(e);
    }

    @Override public Option<A> toOption() {
      return none();
    }

    @Override public Optional<A> toOptional() {
      return Optional.empty();
    }

    @Override public Stream<A> toStream() {
      return Stream.empty();
    }

    @Override public void forEach(Consumer<? super A> action) {}

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

    @Override public String toString() {
      return "Try.Failure(" + e.toString() + ")";
    }
  }

  private static final class Success<A> extends Try<A> {
    private static final long serialVersionUID = -8360076933771852847L;

    private final A value;

    Success(final A value) {
      this.value = requireNonNull(value);
    }

    @Override public <B> Try<B> map(final Function<? super A, ? extends B> f) {
      return Checked.now(() -> f.apply(value));
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

    @Override public <X extends Exception> Try<A> recover(final Class<X> exceptionType, final Function<? super X, A> f) {
      return this;
    }

    @Override public Try<A> recoverWith(final Function<? super Exception, Try<A>> f) {
      return this;
    }

    @Override public <X extends Exception> Try<A> recoverWith(final Class<X> exceptionType, final Function<? super X, Try<A>> f) {
      return this;
    }

    @Override public A getOrElse(final Supplier<A> s) {
      return value;
    }

    @Override public Try<A> orElse(Supplier<? extends Try<? extends A>> orElse) {
      return this;
    }

    @Override public Try<A> filterOrElse(Predicate<? super A> p, Supplier<Exception> orElseSupplier) {
      return Checked.now(() -> {
        if (p.test(value)) {
          return value;
        }
        throw orElseSupplier.get();
      });
    }

    @Override public <B> B fold(final Function<? super Exception, B> failureF, final Function<A, B> successF) {
      return successF.apply(value);
    }

    @Override public Either<Exception, A> toEither() {
      return right(value);
    }

    @Override public Option<A> toOption() {
      return some(value);
    }

    @Override public Optional<A> toOptional() {
      return Optional.of(value);
    }

    @Override public Stream<A> toStream() {
      return Stream.of(value);
    }

    @Override public void forEach(Consumer<? super A> action) {
      action.accept(value);
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

    @Override public String toString() {
      return "Try.Success(" + value.toString() + ")";
    }
  }

  private static final class Delayed<A> extends Try<A> implements Externalizable {
    private static final long serialVersionUID = 2439842151512848666L;

    private final AtomicReference<Function<Unit, Try<A>>> runReference;

    static <A> Delayed<A> fromSupplier(final Supplier<Try<A>> delayed) {
      Supplier<Try<A>> memorized = memoize(delayed);
      return new Delayed<>(unit -> memorized.get());
    }

    public Delayed() {
      this(unit -> {
        throw new IllegalStateException("Try.Delayed() default constructor only required for Serialization. Do not invoke directly.");
      });
    }

    private Delayed(final Function<Unit, Try<A>> run) {
      this.runReference = new AtomicReference<>(run);
    }

    private Function<Unit, Try<A>> getRunner() {
      return this.runReference.get();
    }

    private Try<A> eval() {
      return this.getRunner().apply(Unit());
    }

    @Override public boolean isFailure() {
      return eval().isFailure();
    }

    @Override public boolean isSuccess() {
      return eval().isSuccess();
    }

    private <B> Try<B> composeDelayed(Function<Try<A>, Try<B>> f) {
      return new Delayed<>(f.compose(this.getRunner()));
    }

    @Override public <B> Try<B> flatMap(Function<? super A, Try<B>> f) {
      return composeDelayed(t -> t.flatMap(f));
    }

    @Override public <B> Try<B> map(Function<? super A, ? extends B> f) {
      return composeDelayed(t -> t.map(f));
    }

    @Override public Try<A> recover(Function<? super Exception, A> f) {
      return composeDelayed(t -> t.recover(f));
    }

    @Override public <X extends Exception> Try<A> recover(Class<X> exceptionType, Function<? super X, A> f) {
      return composeDelayed(t -> t.recover(exceptionType, f));
    }

    @Override public Try<A> recoverWith(Function<? super Exception, Try<A>> f) {
      return composeDelayed(t -> t.recoverWith(f));
    }

    @Override public <X extends Exception> Try<A> recoverWith(Class<X> exceptionType, Function<? super X, Try<A>> f) {
      return composeDelayed(t -> t.recoverWith(exceptionType, f));
    }

    @Override public A getOrElse(Supplier<A> s) {
      return eval().getOrElse(s);
    }

    @Override public Try<A> orElse(Supplier<? extends Try<? extends A>> orElse) {
      return composeDelayed(t -> t.orElse(orElse));
    }

    @Override public Try<A> filterOrElse(Predicate<? super A> p, Supplier<Exception> orElseSupplier) {
      return composeDelayed(t -> t.filterOrElse(p, orElseSupplier));
    }

    @Override public <B> B fold(Function<? super Exception, B> failureF, Function<A, B> successF) {
      return eval().fold(failureF, successF);
    }

    @Override public Either<Exception, A> toEither() {
      return eval().toEither();
    }

    @Override public Option<A> toOption() {
      return eval().toOption();
    }

    @Override public Optional<A> toOptional() {
      return eval().toOptional();
    }

    @Override public Stream<A> toStream() {
      return eval().toStream();
    }

    @Override public void forEach(Consumer<? super A> action) {
      eval().forEach(action);
    }

    @Override public void writeExternal(ObjectOutput out) throws IOException {
      // If you required serialization to return this through a remote call, it
      // would seem expected for the expression to be evaluated.
      // Therefore in order to serialize we need to evaluate the Delayed.
      out.writeObject(eval());
    }

    @Override @SuppressWarnings("unchecked") public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      Try<A> result = (Try<A>) in.readObject();
      this.runReference.set(unit -> result);
    }
  }
}
