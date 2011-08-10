package com.atlassian.fugue;

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class that acts as a container for a value of one of two types. An Either
 * will be either {@link Either.Left Left} or {@link Either.Right Right}.
 * <p>
 * Checking which type an Either is can be done by calling the @
 * {@link #isLeft()} and {@link #isRight()} methods.
 * <p>
 * An Either can be used to express a success or failure case. By convention,
 * Right is used to store the success value, (you can use the play on words
 * "right" == "correct" as a mnemonic) and Left is used to store failure values
 * (such as exceptions).
 * <p>
 * While this class is public and abstract it does not expose a constructor as
 * only the concrete Left and Right subclasses are meant to be used.
 * <p>
 * Either is immutable, but do not force immutability on contained objects; if
 * the contained objects are mutable then equals and hashcode methods should not
 * be relied on.
 */
public abstract class Either<L, R> {
  //
  // factory methods
  //

  /**
   * @param left the value to be stored, must not be null
   * @return a Left containing the supplied value
   */
  public static <L, R> Either<L, R> left(final L left) {
    checkNotNull(left);
    return new Left<L, R>(left);
  }

  /**
   * @param right the value to be stored, must not be null
   * @return a Right containing the supplied value
   */
  public static <L, R> Either<L, R> right(final R right) {
    checkNotNull(right);
    return new Right<L, R>(right);
  }

  //
  // static utility methods
  //

  /**
   * Extracts an object from an Either, regardless of the side in which it is
   * stored, provided both sides contain the same type. This method will never
   * return null.
   */
  public static <T> T merge(final Either<T, T> either) {
    if (either.isLeft()) {
      return either.left().get();
    }
    return either.right().get();
  }

  /**
   * Creates an Either based on a boolean expression. If predicate is true, a
   * Right wil be returned containing the supplied right value; if it is false,
   * a Left will be returned containing the supplied left value.
   */
  public static <L, R> Either<L, R> cond(final boolean predicate, final R right, final L left) {
    return (predicate) ? Either.<L, R> right(right) : Either.<L, R> left(left);
  }

  /**
   * Simplifies extracting a value or throwing a checked exception from an
   * Either.
   * 
   * @param <X> the exception type
   * @param <A> the value type
   * @param either to extract from
   * @return the value from the RHS
   * @throws X the exception on the LHS
   */
  public static <X extends Exception, A> A getOrThrow(final Either<X, A> either) throws X {
    if (either.isLeft()) {
      throw either.left().get();
    }
    return either.right().get();
  }

  //
  // constructors
  //

  Either() {}

  //
  // methods
  //

  /**
   * Returns <code>true</code> if this either is a left, <code>false</code>
   * otherwise.
   * 
   * @return <code>true</code> if this either is a left, <code>false</code>
   * otherwise.
   */
  public boolean isLeft() {
    return false;
  }

  /**
   * Returns <code>true</code> if this either is a right, <code>false</code>
   * otherwise.
   * 
   * @return <code>true</code> if this either is a right, <code>false</code>
   * otherwise.
   */
  public boolean isRight() {
    return false;
  }

  /**
   * Projects this either as a right.
   * 
   * @return A right projection of this either.
   */
  public final LeftProjection left() {
    return new LeftProjection();
  }

  /**
   * Projects this either as a right.
   * 
   * @return A right projection of this either.
   */
  public final RightProjection right() {
    return new RightProjection();
  }

  // value accessor for Left
  L getLeft() {
    throw new NoSuchElementException();
  }

  // value accessor for Right
  R getRight() {
    throw new NoSuchElementException();
  }

  /**
   * If this is a left, then return the left value in right, or vice versa.
   * 
   * @return an Either that is a Left if this is a Right or a Right if this is a
   * Left. The value remains the same.
   */
  public abstract Either<R, L> swap();

  /**
   * Applies the function to the wrapped value, applying ifLeft it this is a
   * Left and ifRight if this is a Right.
   * 
   * @param ifLeft the function to apply if this is a Left
   * @param ifRight the function to apply if this is a Right
   * @return the result of the applies function
   */
  public abstract <V> V fold(Function<? super L, V> ifLeft, Function<? super R, V> ifRight);

  //
  // inner class implementations
  //

  static final class Left<L, R> extends Either<L, R> {
    private final L value;

    public Left(final L value) {
      checkNotNull(value);
      this.value = value;
    }

    @Override final L getLeft() {
      return value;
    }

    @Override public boolean isLeft() {
      return true;
    }

    @Override public Either<R, L> swap() {
      return right(value);
    }

    @Override public <V> V fold(final Function<? super L, V> ifLeft, final Function<? super R, V> ifRight) {
      return ifLeft.apply(value);
    }

    @Override public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if ((o == null) || !(o instanceof Left<?, ?>)) {
        return false;
      }
      return value.equals(((Left<?, ?>) o).value);
    }

    @Override public int hashCode() {
      return value.hashCode();
    }

    @Override public String toString() {
      return "Either.Left(" + value.toString() + ")";
    }
  }

  static final class Right<L, R> extends Either<L, R> {
    private final R value;

    public Right(final R value) {
      checkNotNull(value);
      this.value = value;
    }

    @Override final R getRight() {
      return value;
    }

    @Override public boolean isRight() {
      return true;
    }

    @Override public Either<R, L> swap() {
      return left(value);
    }

    @Override public <V> V fold(final Function<? super L, V> ifLeft, final Function<? super R, V> ifRight) {
      return ifRight.apply(value);
    }

    @Override public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if ((o == null) || !(o instanceof Right<?, ?>)) {
        return false;
      }
      return value.equals(((Right<?, ?>) o).value);
    }

    @Override public int hashCode() {
      return value.hashCode();
    }

    @Override public String toString() {
      return "Either.Right(" + value.toString() + ")";
    }
  }

  /**
   * A left projection of an either value.
   */
  public final class LeftProjection implements Projection<L, L, R> {
    private LeftProjection() {}

    public Iterator<L> iterator() {
      return fold(Functions.<L> singletonIterator(), Functions.<L, R> emptyIterator());
    }

    public Either<L, R> either() {
      return Either.this;
    }

    public L get() {
      return getLeft();
    }

    @Override public L getOrNull() {
      return isLeft() ? get() : null;
    }

    @Override public boolean isDefined() {
      return isLeft();
    }

    @Override public boolean isEmpty() {
      return !isDefined();
    }

    public L getOrError(final Supplier<String> err) {
      return toOption().getOrError(err);
    }

    public L getOrElse(final Supplier<L> a) {
      return isLeft() ? get() : a.get();
    }

    public <X extends L> L getOrElse(final X x) {
      return isLeft() ? get() : x;
    }

    public void foreach(final Effect<L> f) {
      if (isLeft()) {
        f.apply(get());
      }
    }

    public boolean forall(final Predicate<L> f) {
      return isRight() || f.apply(get());
    }

    public boolean exists(final Predicate<L> f) {
      return isLeft() && f.apply(get());
    }

    public Option<L> toOption() {
      return isLeft() ? some(get()) : Option.<L> none();
    }

    //
    // stuff that can't be put on an interface without HKT
    //

    /**
     * The value of this projection or the result of the given function on the
     * opposing projection's value.
     * 
     * @param f The function to execute if this projection has no value.
     * @return The value of this projection or the result of the given function
     * on the opposing projection's value.
     */
    public L on(final Function<? super R, L> f) {
      return isLeft() ? get() : f.apply(right().get());
    }

    /**
     * Map the given function across this projection's value if it has one.
     * 
     * @param f The function to map across this projection.
     * @return A new either value after mapping.
     */
    public <X> Either<X, R> map(final Function<? super L, X> f) {
      return isLeft() ? new Left<X, R>(f.apply(get())) : new Right<X, R>(right().get());
    }

    /**
     * Binds the given function across this projection's value if it has one.
     * 
     * @param f The function to bind across this projection.
     * @return A new either value after binding.
     */
    public <X> Either<X, R> flatMap(final Function<? super L, Either<X, R>> f) {
      return isLeft() ? f.apply(get()) : new Right<X, R>(getRight());
    }

    /**
     * Anonymous bind through this projection.
     * 
     * @param e The value to bind with.
     * @return An either after binding through this projection.
     */
    public <X> Either<X, R> sequence(final Either<X, R> e) {
      return flatMap(Functions.<L, Either<X, R>> constant(e));
    }

    /**
     * Returns <code>None</code> if this projection has no value or if the given
     * predicate <code>p</code> does not hold for the value, otherwise, returns
     * a right in <code>Some</code>.
     * 
     * @param f The predicate function to test on this projection's value.
     * @return <code>None</code> if this projection has no value or if the given
     * predicate <code>p</code> does not hold for the value, otherwise, returns
     * a right in <code>Some</code>.
     */
    public <X> Option<Either<L, X>> filter(final Predicate<L> f) {
      if (isLeft() && f.apply(get())) {
        return Option.<Either<L, X>> some(new Left<L, X>(get()));
      }
      return none();
    }

    /**
     * Function application on this projection's value.
     * 
     * @param either The either of the function to apply on this projection's
     * value.
     * @return The result of function application within either.
     */
    public <X> Either<X, R> apply(final Either<Function<L, X>, R> either) {
      return either.left().flatMap(new Function<Function<L, X>, Either<X, R>>() {
        public Either<X, R> apply(final Function<L, X> f) {
          return map(f);
        }
      });
    }
  }

  /**
   * A right projection of an either value.
   */
  public final class RightProjection implements Projection<R, L, R> {
    private RightProjection() {}

    public Iterator<R> iterator() {
      return toOption().iterator();
    }

    public Either<L, R> either() {
      return Either.this;
    }

    public R getOrError(final Supplier<String> err) {
      return toOption().getOrError(err);
    }

    public R get() {
      return getRight();
    }

    @Override public boolean isDefined() {
      return isRight();
    }

    @Override public boolean isEmpty() {
      return !isDefined();
    }

    public R getOrElse(final Supplier<R> b) {
      return isRight() ? get() : b.get();
    }

    public <X extends R> R getOrElse(final X x) {
      return isRight() ? get() : x;
    }

    public R getOrNull() {
      return isRight() ? get() : null;
    }

    public void foreach(final Effect<R> f) {
      if (isRight()) {
        f.apply(get());
      }
    }

    public boolean forall(final Predicate<R> f) {
      return isLeft() || f.apply(get());
    }

    public boolean exists(final Predicate<R> f) {
      return isRight() && f.apply(get());
    }

    public Option<R> toOption() {
      return isRight() ? some(get()) : Option.<R> none();
    }

    //
    // stuff that can't be made put on an interface without HKT
    //

    /**
     * The value of this projection or the result of the given function on the
     * opposing projection's value.
     * 
     * @param f The function to execute if this projection has no value.
     * @return The value of this projection or the result of the given function
     * on the opposing projection's value.
     */
    public R on(final Function<? super L, R> f) {
      return isRight() ? get() : f.apply(left().get());
    }

    /**
     * Map the given function across this projection's value if it has one.
     * 
     * @param f The function to map across this projection.
     * @return A new either value after mapping.
     */
    public <X> Either<L, X> map(final Function<? super R, X> f) {
      return isRight() ? new Right<L, X>(f.apply(get())) : this.<X> toLeft();
    }

    <X> Left<L, X> toLeft() {
      return new Left<L, X>(left().get());
    }

    /**
     * Binds the given function across this projection's value if it has one.
     * 
     * @param f The function to bind across this projection.
     * @return A new either value after binding.
     */
    public <X> Either<L, X> flatMap(final Function<R, Either<L, X>> f) {
      return isRight() ? f.apply(get()) : new Left<L, X>(left().get());
    }

    /**
     * Anonymous bind through this projection.
     * 
     * @param e The value to bind with.
     * @return An either after binding through this projection.
     */
    public <X> Either<L, X> sequence(final Either<L, X> e) {
      return flatMap(Functions.<R, Either<L, X>> constant(e));
    }

    /**
     * Returns <code>None</code> if this projection has no value or if the given
     * predicate <code>p</code> does not hold for the value, otherwise, returns
     * a left in <code>Some</code>.
     * 
     * @param f The predicate function to test on this projection's value.
     * @return <code>None</code> if this projection has no value or if the given
     * predicate <code>p</code> does not hold for the value, otherwise, returns
     * a left in <code>Some</code>.
     */
    public <X> Option<Either<X, R>> filter(final Predicate<R> f) {
      if (isRight() && f.apply(get())) {
        return Option.<Either<X, R>> some(new Right<X, R>(get()));
      }
      return Option.<Either<X, R>> none();
    }

    /**
     * Function application on this projection's value.
     * 
     * @param either The either of the function to apply on this projection's
     * value.
     * @return The result of function application within either.
     */
    public <X> Either<L, X> apply(final Either<L, Function<R, X>> either) {
      return either.right().flatMap(new Function<Function<R, X>, Either<L, X>>() {
        public Either<L, X> apply(final Function<R, X> f) {
          return map(f);
        }
      });
    }
  }

  public interface Projection<A, L, R> extends Maybe<A> {
    /**
     * The either value underlying this projection.
     * 
     * @return The either value underlying this projection.
     */
    Either<L, R> either();

    /**
     * Returns this projection's value in <code>Some</code> if it exists,
     * otherwise <code>None</code>.
     * 
     * @return This projection's value in <code>Some</code> if it exists,
     * otherwise <code>None</code>.
     */
    Option<A> toOption();
  }
}
