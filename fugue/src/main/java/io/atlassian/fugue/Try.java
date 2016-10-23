package io.atlassian.fugue;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

/**
 * A <code>Try</code> represents a computation that may either throw an
 * exception or result in a value. A Try will either be {@link Try.Success
 * Success} wrapping a value or {@link Try.Failure Failure} which wraps an
 * exception.
 * <p>
 * This class is similar to {@link Either}, but is explicit about having a success and failure case.
 * Unless method level javadoc says otherwise, methods will not automatically catch exceptions thrown by function arguments.
 * In particular map will not catch automatically catch thrown exceptions,
 * instead you should use {@link Try#lift} to to make the function explicitly return a Try and the use flatmap.
 */
public abstract class Try<T> {

    /**
     * Represents a {@link Function} that may throw an exception.
     *
     * @param <T> the type of the input to the function
     * @param <R> the type of the result of the function
     */
    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }

    /**
     * Represents a {@link Supplier} that may throw an exception.
     *
     * @param <T> the type of the result of the supplier
     */
    @FunctionalInterface
    public interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Lift a function that potentially throws in a function that either returns a
     * Success of the value or a failure containing the thrown exception.
     *
     * @param f   a function that can throw
     * @param <T>
     * @param <R>
     * @return a function that either returns a Success of the value or a failure
     * containing the thrown exception.
     */
    public static <T, R> Function<T, Try<R>> lift(CheckedFunction<T, R> f) {
        return t -> Try.of(() -> f.apply(t));
    }

    /**
     * Create a new Try representing the result of a potentially exception
     * throwing operation. If the provided supplier throws an exception this will
     * return a failure wrapping the exception, otherwise a success of the
     * supplied value will be returned.
     *
     * @param s   a supplier that may throw an exception
     * @param <U> the type of value s supplies
     * @return If s throws an exception this will return a failure wrapping the
     * exception, otherwise a success of the supplied value/.
     */
    public static <U> Try<U> of(CheckedSupplier<U> s) {
        try {
            return successful(s.get());
        } catch (final Exception e) {
            return failure(e);
        }
    }

    /**
     * Creates a new failure
     *
     * @param e   an exception to wrap, must not be null.
     * @param <U> the success type
     * @return a new Failure wrapping e.
     */
    public static <U> Try<U> failure(final Exception e) {
        requireNonNull(e);
        return new Failure<>(e);
    }

    /**
     * Creates a new Success
     *
     * @param v   a value to wrap, must not be null
     * @param <U> the wrapped value type
     * @return a new Success wrapping v
     */
    public static <U> Try<U> successful(final U v) {
        requireNonNull(v);
        return new Success<>(v);
    }

    /**
     * Returns a success wrapping all of the values if all of the arguments were a
     * success, otherwise this returns the first failure
     *
     * @param trys an iterable of try values
     * @param <T>  The success type
     * @return a success wrapping all of the values if all of the arguments were a
     * success, otherwise this returns the first failure
     */
    public static <T> Try<Iterable<T>> sequence(Iterable<Try<T>> trys) {
        final ArrayList<T> ts = new ArrayList<>();
        for (final Try<T> t : trys) {
            if (t.isFailure()) {
                return new Failure<>(t.getExceptionUnsafe());
            }
            ts.add(t.getUnsafe());
        }
        return new Success<>(ts);
    }

    /**
     * reduces a nested Try by a single level
     *
     * @param t A nested Try
     * @param <T> The success type
     * @return The flattened try
     */
    public static <T> Try<T> flatten(Try<Try<T>> t) {
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
     * @param <U> result type
     * @param f   the function to bind.
     * @return A new Try value after binding with the function applied if this is
     * a Success, otherwise returns this if this is a `Failure`.
     */
    public abstract <U> Try<U> flatMap(Function<? super T, Try<U>> f);

    /**
     * Maps the given function to the value from this `Success` or returns this if
     * this is a `Failure`.
     *
     * @param <U> result type
     * @param f   the function to apply
     * @return `f` applied to the `Success`, otherwise returns this if this is a
     * `Failure`.
     */
    public abstract <U> Try<U> map(Function<? super T, ? extends U> f);

    /**
     * Applies the given function `f` if this is a `Failure`. This is like map for
     * the exception.
     *
     * @param f the function to apply
     * @return `f` applied to the `Failure`, otherwise returns this if this is a
     * `Success`.
     */
    public abstract Try<T> recover(Function<? super Exception, T> f);

    /**
     * Binds the given function across the failure value if it is one. This is
     * like flatmap for the exception.
     *
     * @param f the function to bind.
     * @return A new Try value after binding with the function applied if this is
     * a Success, otherwise returns this if this is a `Failure`.
     */
    public abstract Try<T> recoverWith(Function<? super Exception, Try<T>> f);

    /**
     * Returns the contained value if this is a success otherwise call the
     * supplier and return its value.
     *
     * @param s called if this is a failure
     * @return the wrapped value or the value from the {@code Supplier}
     */
    public abstract T getOrElse(Supplier<T> s);

    /**
     * Applies the function to the wrapped value, applying failureF it this is a
     * Left and successF if this is a Right.
     *
     * @param failureF the function to apply if this is a Failure
     * @param successF the function to apply if this is a Success
     * @param <U>      the destination type
     * @return the result of the applied function
     */
    public abstract <U> U fold(Function<? super Exception, U> failureF, Function<T, U> successF);

    /**
     * Returns the wrapped value if this is a success, otherwise throws an
     * exception. It is not recommended to directly call this.
     *
     * @return the wrapped success value
     */
    public abstract T getUnsafe();

    /**
     * Returns the wrapped exception if this is a failure, otherwise throws an
     * exception. It is not recommended to directly call this.
     *
     * @return the wrapped exception
     */
    public abstract Exception getExceptionUnsafe();

    /**
     * Convert this Try to an {@link Either}, becoming a left if this is a failure
     * and a right if this is a success.
     *
     * @return this value wrapped in right if a success, and the exception wrapped
     * in a left if a failure.
     */
    public abstract Either<Exception, T> toEither();

    /**
     * Convert this Try to an Option. Returns <code>Some</code> with a value if it
     * is a success, otherwise <code>None</code>.
     *
     * @return The success's value in <code>Some</code> if it exists, otherwise
     * <code>None</code>
     */
    public abstract Option<T> toOption();

    private static final class Failure<T> extends Try<T> {

        private final Exception e;

        public Failure(final Exception e) {
            requireNonNull(e);
            this.e = e;
        }

        @Override
        public <U> Try<U> map(final Function<? super T, ? extends U> f) {
            return new Failure<>(e);
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public <U> Try<U> flatMap(final Function<? super T, Try<U>> f) {
            return Try.failure(e);
        }

        @Override
        public Try<T> recover(final Function<? super Exception, T> f) {
            return Try.of(() -> f.apply(e));
        }

        @Override
        public Try<T> recoverWith(final Function<? super Exception, Try<T>> f) {
            return f.apply(e);
        }

        @Override
        public T getOrElse(final Supplier<T> s) {
            return s.get();
        }

        @Override
        public <U> U fold(final Function<? super Exception, U> failureF, final Function<T, U> successF) {
            return failureF.apply(e);
        }

        @Override
        public T getUnsafe() {
            throw new NoSuchElementException();
        }

        @Override
        public Exception getExceptionUnsafe() {
            return e;
        }

        @Override
        public Either<Exception, T> toEither() {
            return Either.left(e);
        }

        @Override
        public Option<T> toOption() {
            return Option.none();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Failure<?> failure = (Failure<?>) o;

            return e != null ? e.equals(failure.e) : failure.e == null;
        }

        @Override
        public int hashCode() {
            return ~e.hashCode();
        }
    }

    private static final class Success<T> extends Try<T> {

        private final T value;

        public Success(final T value) {
            requireNonNull(value);
            this.value = value;
        }

        @Override
        public <U> Try<U> map(final Function<? super T, ? extends U> f) {
            U val = f.apply(value);
            return Try.of(() -> val);
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public <U> Try<U> flatMap(final Function<? super T, Try<U>> f) {
            return f.apply(value);
        }

        @Override
        public Try<T> recover(final Function<? super Exception, T> f) {
            return this;
        }

        @Override
        public Try<T> recoverWith(final Function<? super Exception, Try<T>> f) {
            return this;
        }

        @Override
        public T getOrElse(final Supplier<T> s) {
            return value;
        }

        @Override
        public <U> U fold(final Function<? super Exception, U> failureF, final Function<T, U> successF) {
            return successF.apply(value);
        }

        @Override
        public T getUnsafe() {
            return value;
        }

        @Override
        public Exception getExceptionUnsafe() {
            throw new NoSuchElementException();
        }

        @Override
        public Either<Exception, T> toEither() {
            return Either.right(value);
        }

        @Override
        public Option<T> toOption() {
            return Option.some(value);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Success<?> success = (Success<?>) o;
            return value != null ? value.equals(success.value) : success.value == null;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}
