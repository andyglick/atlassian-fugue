package io.atlassian.fugue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static io.atlassian.fugue.Suppliers.memoize;
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
 *
 * @since 4.4.0
 */
@SuppressWarnings("WeakerAccess")
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
        return new Failure<>(t.fold(identity(), x -> {
          throw new NoSuchElementException();
        }));
      }
      collector.accumulator().accept(accumulator, t.fold(f -> {
        throw new NoSuchElementException();
      }, identity()));
    }
    return new Success<>(collector.finisher().apply(accumulator));
  }

  public static <A> SequenceCollector<A> sequenceCollector() {
    return new SequenceCollector<>();
  }

  public static class SequenceCollector<A> implements Collector<Try<A>, SequenceCollector.ResultContainer<A>, Try<Iterable<A>>> {

    static class ResultContainer<A> {
      private volatile Try<List<A>> result;

      private ResultContainer() {
        this(new ArrayList<>());
      }

      private ResultContainer(List<A> mutableList) {
        this.result = Try.successful(mutableList);
      }

      private ResultContainer(Try<List<A>> ts) {
        this.result = ts;
      }

      private void add(Try<A> t) {
        this.result = result.flatMap(as -> {
          if (t.isFailure()) {
            return Try.failure(t.fold(identity(), a -> {
              throw new NoSuchElementException();
            }));
          }
          as.add(t.fold(e -> {
            throw new NoSuchElementException();
          }, identity()));
          return Try.successful(as);
        });
      }

      private ResultContainer<A> append(ResultContainer<A> other) {
        return new ResultContainer<>(this.result.flatMap(as1 -> other.result.flatMap(as2 -> {
          final List<A> combined = new ArrayList<>(as1);
          combined.addAll(as2);
          return Try.successful(combined);
        })));
      }

      private Try<Iterable<A>> get() {
        return this.result.map(as -> {
          final ArrayList<A> copy = new ArrayList<>(as);
          return Collections.unmodifiableList(copy);
        });
      }
    }

    @Override
    public Supplier<ResultContainer<A>> supplier() {
      return ResultContainer::new;
    }

    @Override
    public BiConsumer<ResultContainer<A>, Try<A>> accumulator() {
      return ResultContainer::add;
    }

    @Override
    public BinaryOperator<ResultContainer<A>> combiner() {
      return ResultContainer::append;
    }

    @Override
    public Function<ResultContainer<A>, Try<Iterable<A>>> finisher() {
      return ResultContainer::get;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Collections.emptySet();
    }
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
   * Applies the given function `f` if this is a `Failure` with certain
   * exception type otherwise leaves this unchanged. This is like map for
   * exceptions types.
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
   *
   * @param f the function to bind.
   * @return A new Try value after binding with the function applied if this is
   * a `Failure`, otherwise returns this if this is a `Success`.
   */
  public abstract Try<A> recoverWith(Function<? super Exception, Try<A>> f);

  /**
   * Binds the given function across certain exception type if it is one,
   * otherwise this unchanged. This is like flatmap for exceptions types.
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

    @SuppressWarnings("unchecked") @Override public <X extends Exception> Try<A> recover(final Class<X> exceptionType, final Function<? super X, A> f) {
      return exceptionType.isAssignableFrom(e.getClass()) ? Checked.of(() -> f.apply((X) e)) : this;
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

  private static final class Delayed<A> extends Try<A> {

    private final Function<Unit, Try<A>> run;

    static <A> Delayed<A> fromSupplier(final Supplier<Try<A>> delayed) {
      Supplier<Try<A>> memorized = memoize(delayed);
      return new Delayed<>(unit -> memorized.get());
    }

    private Delayed(final Function<Unit, Try<A>> run) {
      this.run = run;
    }

    private Try<A> eval() {
      return this.run.apply(Unit.VALUE);
    }

    @Override
    public boolean isFailure() {
      return eval().isFailure();
    }

    @Override
    public boolean isSuccess() {
      return eval().isSuccess();
    }

    private <B> Try<B> composeDelayed(Function<Try<A>, Try<B>> f) {
      return new Delayed<>(f.compose(this.run));
    }

    @Override
    public <B> Try<B> flatMap(Function<? super A, Try<B>> f) {
      return composeDelayed(t -> t.flatMap(f));
    }

    @Override
    public <B> Try<B> map(Function<? super A, ? extends B> f) {
      return composeDelayed(t -> t.map(f));
    }

    @Override
    public Try<A> recover(Function<? super Exception, A> f) {
      return composeDelayed(t -> t.recover(f));
    }

    @Override
    public <X extends Exception> Try<A> recover(Class<X> exceptionType, Function<? super X, A> f) {
      return composeDelayed(t -> t.recover(exceptionType, f));
    }

    @Override
    public Try<A> recoverWith(Function<? super Exception, Try<A>> f) {
      return composeDelayed(t -> t.recoverWith(f));
    }

    @Override
    public <X extends Exception> Try<A> recoverWith(Class<X> exceptionType, Function<? super X, Try<A>> f) {
      return composeDelayed(t -> t.recoverWith(exceptionType, f));
    }

    @Override
    public A getOrElse(Supplier<A> s) {
      return eval().getOrElse(s);
    }

    @Override
    public <B> B fold(Function<? super Exception, B> failureF, Function<A, B> successF) {
      return eval().fold(failureF, successF);
    }

    @Override
    public Either<Exception, A> toEither() {
      return eval().toEither();
    }

    @Override
    public Option<A> toOption() {
      return eval().toOption();
    }
  }
}
