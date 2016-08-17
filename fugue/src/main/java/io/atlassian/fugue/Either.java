/*
   Copyright 2011 Atlassian

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package io.atlassian.fugue;

import static io.atlassian.fugue.Option.none;
import static io.atlassian.fugue.Option.some;
import static io.atlassian.fugue.Suppliers.ofInstance;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
 * Either is immutable, but does not force immutability on contained objects; if
 * the contained objects are mutable then equals and hashcode methods should not
 * be relied on.
 * <p>
 * Since 2.2, there have been some right-biased methods added. With 2.3 the
 * available right-biased methods has increased. The purpose of these is that
 * you can do something like {@code either.map(...)} directly, which is
 * identical to calling {@code either.right().map(...)}.
 *
 * @since 1.0
 */
public abstract class Either<L, R> implements Serializable {
  private static final long serialVersionUID = -1L;

  //
  // factory methods
  //

  /**
   * <p>
   * left.
   * </p>
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param left the value to be stored, must not be null
   * @return a Left containing the supplied value
   * @since 1.0
   */
  public static <L, R> Either<L, R> left(final L left) {
    requireNonNull(left);
    return new Left<>(left);
  }

  /**
   * <p>
   * right.
   * </p>
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param right the value to be stored, must not be null
   * @return a Right containing the supplied value
   * @since 1.0
   */
  public static <L, R> Either<L, R> right(final R right) {
    requireNonNull(right);
    return new Right<>(right);
  }

  //
  // constructors
  //

  Either() {}

  //
  // methods
  //

  /**
   * Projects this either as a left.
   *
   * @return A left projection of this either.
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

  // right-bias

  /**
   * Get the value if it is a right or call the supplier and return its value if
   * not.
   *
   * @param supplier called if this is a left
   * @return the wrapped value or the value from the {@code Supplier}
   * @since 4.3
   */
  public final R getOr(final Supplier<? extends R> supplier) {
    return right().getOr(supplier);
  }

  /**
   * Get the value if it is a right or call the supplier and return its value if
   * not.
   *
   * @param supplier called if this is a left
   * @return the wrapped value or the value from the {@code Supplier}
   * @since 2.3
   * @deprecated since 4.3, use {@link #getOr(Supplier)} instead
   */
  @Deprecated public final R getOrElse(final Supplier<? extends R> supplier) {
    return getOr(supplier);
  }

  /**
   * Get the value if it is a right, otherwise returns {@code other}.
   *
   * @param <X> default type
   * @param other value to return if this is a left
   * @return wrapped value if this is a right, otherwise returns {@code other}
   * @since 2.3
   */
  public final <X extends R> R getOrElse(final X other) {
    return right().getOrElse(other);
  }

  /**
   * Get the value if it is right or null if not.
   * <p>
   * Although the use of null is discouraged, code written to use these must
   * often interface with code that expects and returns nulls.
   *
   * @return the contained value or null
   * @since 2.3
   */
  public final R getOrNull() {
    return right().getOrNull();
  }

  /**
   * Get the contained value or throws an error with the supplied message if
   * left.
   * <p>
   * Used when absolutely sure this is a right.
   *
   * @param msg the message for the error.
   * @return the contained value.
   * @since 2.3
   */
  public final R getOrError(final Supplier<String> msg) {
    return right().getOrError(msg);
  }

  /**
   * Get the contained value or throws the supplied throwable if left
   * <p>
   * Used when absolutely sure this is a right.
   *
   * @param <X> exception type
   * @param ifUndefined the supplier of the throwable.
   * @return the contained value.
   * @throws X the throwable the supplier creates if there is no value.
   * @since 2.3
   */
  public final <X extends Throwable> R getOrThrow(final Supplier<X> ifUndefined) throws X {
    return right().getOrThrow(ifUndefined);
  }

  /**
   * Map the given function across the right hand side value if it is one.
   *
   * @param <X> the RHS type
   * @param f The function to map .
   * @return A new either value after mapping with the function applied if this
   * is a Right.
   * @since 2.2
   */
  public final <X> Either<L, X> map(final Function<? super R, X> f) {
    return right().map(f);
  }

  /**
   * Binds the given function across the right hand side value if it is one.
   *
   * @param <X> the RHS type
   * @param <LL> result type
   * @param f the function to bind.
   * @return A new either value after binding with the function applied if this
   * is a Right.
   * @since 2.2
   */
  public final <X, LL extends L> Either<L, X> flatMap(final Function<? super R, Either<LL, X>> f) {
    return right().flatMap(f);
  }

  /**
   * Return `true` if this is a right value <strong>and</strong> applying the
   * predicate to the contained value returns true.
   *
   * @param p the predicate to test.
   * @return {@code true} if right and the predicate returns true for the right
   * value, {@code false} otherwise.
   * @since 2.3
   */
  public final boolean exists(final Predicate<? super R> p) {
    return right().exists(p);
  }

  /**
   * Returns <code>true</code> if it is a left or the result of the application
   * of the given predicate on the contained value.
   *
   * @param p The predicate function to test on the contained value.
   * @return <code>true</code> if no value or returns the result of the
   * application of the given function to the value.
   * @since 2.3
   */
  public final boolean forall(final Predicate<? super R> p) {
    return right().forall(p);
  }

  /**
   * Perform the given side-effect for the contained element if it is a right
   *
   * @param effect the input to use for performing the effect on contained
   * value.
   * @since 2.3
   * @deprecated use {@link #forEach(Consumer)} instead
   */
  @Deprecated public final void foreach(final Effect<? super R> effect) {
    right().foreach(effect);
  }

  /**
   * Perform the given side-effect for the contained element if it is a right
   *
   * @param effect the input to use for performing the effect on contained
   * value.
   * @since 3.1
   */
  public final void forEach(final Consumer<? super R> effect) {
    right().forEach(effect);
  }

  /**
   * If this is a right, return the same right. Otherwise, return {@code orElse}
   * .
   *
   * @param orElse either to return if this is left
   * @return this or {@code orElse}
   * @since 2.3
   */
  public final Either<L, R> orElse(final Either<? extends L, ? extends R> orElse) {
    return this.orElse(ofInstance(orElse));
  }

  /**
   * If this is a right, return the same right. Otherwise, return value supplied
   * by {@code orElse}.
   *
   * @param orElse either to return if this is left
   * @return this or {@code orElse}
   * @since 2.3
   */
  public final Either<L, R> orElse(final Supplier<? extends Either<? extends L, ? extends R>> orElse) {
    if (right().isDefined()) {
      return new Right<>(right().get());
    }

    @SuppressWarnings("unchecked")
    final Either<L, R> result = (Either<L, R>) orElse.get();
    return result;
  }

  /**
   * If this is a right return the contained value, else return the result of
   * running function on left
   *
   * @param or Function to run if this is a left
   * @return contained value of R or result of {@code or}
   * @since 2.3
   * @deprecated In favor of {@code rightOr}
   * @see Either#rightOr(Function)
   */
  @Deprecated public final R valueOr(final Function<L, ? extends R> or) {
    return rightOr(or);
  }

  /**
   * If this is a right return the contained value, else return the result of
   * applying {@code leftTransformer} on left
   *
   * @param leftTransformer Function to run on left if this is a left
   * @return contained value of right or result of {@code leftTransformer}
   * @since 3.2
   */
  public final R rightOr(final Function<L, ? extends R> leftTransformer) {
    if (right().isDefined()) {
      return right().get();
    }

    return leftTransformer.apply(left().get());
  }

  /**
   * If this is a left return the contained value, else return the result of
   * applying {@code rightTransformer} on right
   *
   * @param rightTransformer Function to run on right if this is a right
   * @return contained value of left or result of {@code rightTransformer}
   * @since 3.2
   */
  public final L leftOr(final Function<R, ? extends L> rightTransformer) {
    if (left().isDefined()) {
      return left().get();
    }

    return rightTransformer.apply(right().get());
  }

  /**
   * Returns <code>None</code> if this is a left or if the given predicate
   * <code>p</code> does not hold for the contained value, otherwise, returns a
   * right in <code>Some</code>.
   *
   * @param p The predicate function to test on the right contained value.
   * @return <code>None</code> if this is a left or if the given predicate
   * <code>p</code> does not hold for the right contained value, otherwise,
   * returns a right in <code>Some</code>.
   * @since 2.3
   */
  public final Option<Either<L, R>> filter(final Predicate<? super R> p) {
    return right().filter(p);
  }

  /**
   * Convert this Either to an {@link Optional}. Returns with
   * {@link Optional#of(Object)} if it is a right, otherwise
   * {@link Optional#empty()}.
   *
   * @return The right projection's value in <code>of</code> if it exists,
   * otherwise <code>empty</code>.
   * @since 4.0
   */
  public final Optional<R> toOptional() {
    return right().toOptional();
  }

  /**
   * Convert this Either to an Option. Returns <code>Some</code> with a value if
   * it is a right, otherwise <code>None</code>.
   *
   * @return The right projection's value in <code>Some</code> if it exists,
   * otherwise <code>None</code>.
   * @since 2.6
   */
  public final Option<R> toOption() {
    return right().toOption();
  }

  /**
   * Will return the supplied Either if this one is right, otherwise this one if
   * left.
   *
   * @param <X> the RHS type
   * @param e The value to bind with.
   * @return An either after binding through this projection.
   * @since 2.6
   */
  public <X> Either<L, X> sequence(final Either<L, X> e) {
    return right().sequence(e);
  }

  /**
   * Given a right containing a function from the right type {@code <R>} to a
   * new type {@code <X>} apply that function to the value inside this either.
   * When any of the input values are left return that left value.
   *
   * @param <X> the RHS type
   * @param either The either of the function to apply if this is a Right.
   * @return The result of function application within either.
   * @since 2.6
   * @deprecated since 3.0 see {@link #ap}
   */
  @Deprecated public <X> Either<L, X> apply(final Either<L, Function<R, X>> either) {
    return ap(either);
  }

  /**
   * Function application on this projection's value.
   *
   * @param <X> the RHS type
   * @param either The either of the function to apply on this projection's
   * value.
   * @return The result of function application within either.
   * @since 3.0
   */
  public <X> Either<L, X> ap(final Either<L, Function<R, X>> either) {
    return either.right().flatMap(this::map);
  }

  /**
   * Map the given function across the left hand side value if it is one.
   *
   * @param <X> the LHS type
   * @param f The function to map.
   * @return A new either value after mapping with the function applied if this
   * is a Left.
   * @since 2.2
   */
  public final <X> Either<X, R> leftMap(final Function<? super L, X> f) {
    return left().map(f);
  }

  //
  // abstract stuff
  //

  /**
   * Returns <code>true</code> if this either is a left, <code>false</code>
   * otherwise.
   *
   * @return <code>true</code> if this either is a left, <code>false</code>
   * otherwise.
   */
  public abstract boolean isLeft();

  /**
   * Returns <code>true</code> if this either is a right, <code>false</code>
   * otherwise.
   *
   * @return <code>true</code> if this either is a right, <code>false</code>
   * otherwise.
   */
  public abstract boolean isRight();

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
   * @param <V> the destination type
   * @param ifLeft the function to apply if this is a Left
   * @param ifRight the function to apply if this is a Right
   * @return the result of the applies function
   */
  public abstract <V> V fold(Function<? super L, V> ifLeft, Function<? super R, V> ifRight);

  /**
   * Map the given functions across the appropriate side.
   *
   * @param <LL> the LHS type
   * @param <RR> the RHS type
   * @param ifLeft The function to map if this Either is a left.
   * @param ifRight The function to map if this Either is a right.
   * @return A new either value after mapping with the appropriate function
   * applied.
   * @since 2.2
   */
  public abstract <LL, RR> Either<LL, RR> bimap(final Function<? super L, ? extends LL> ifLeft, final Function<? super R, ? extends RR> ifRight);

  //
  // internal only, should not be accessed from outside this class
  //

  // value accessor for Left
  L getLeft() {
    throw new NoSuchElementException();
  }

  // value accessor for Right
  R getRight() {
    throw new NoSuchElementException();
  }

  //
  // inner class implementations
  //

  static final class Left<L, R> extends Either<L, R> {
    private static final long serialVersionUID = -6846704510630179771L;

    private final L value;

    public Left(final L value) {
      requireNonNull(value);
      this.value = value;
    }

    @Override final L getLeft() {
      return value;
    }

    @Override public boolean isLeft() {
      return true;
    }

    @Override public boolean isRight() {
      return false;
    }

    @Override public Either<R, L> swap() {
      return right(value);
    }

    @Override public <V> V fold(final Function<? super L, V> ifLeft, final Function<? super R, V> ifRight) {
      return ifLeft.apply(value);
    }

    @Override public <LL, RR> Either<LL, RR> bimap(final Function<? super L, ? extends LL> ifLeft, final Function<? super R, ? extends RR> ifRight) {
      @SuppressWarnings("unchecked")
      final Either<LL, RR> map = (Either<LL, RR>) left().map(ifLeft);
      return map;
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
      return ~value.hashCode();
    }

    @Override public String toString() {
      return "Either.Left(" + value.toString() + ")";
    }
  }

  static final class Right<L, R> extends Either<L, R> {
    private static final long serialVersionUID = 5025077305715784930L;

    private final R value;

    public Right(final R value) {
      requireNonNull(value);
      this.value = value;
    }

    @Override final R getRight() {
      return value;
    }

    @Override public boolean isRight() {
      return true;
    }

    @Override public boolean isLeft() {
      return false;
    }

    @Override public Either<R, L> swap() {
      return left(value);
    }

    @Override public <V> V fold(final Function<? super L, V> ifLeft, final Function<? super R, V> ifRight) {
      return ifRight.apply(value);
    }

    @Override public <LL, RR> Either<LL, RR> bimap(final Function<? super L, ? extends LL> ifLeft, final Function<? super R, ? extends RR> ifRight) {
      @SuppressWarnings("unchecked")
      final Either<LL, RR> map = (Either<LL, RR>) right().map(ifRight);
      return map;
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
   * Holds the common implementation for both projections.
   */
  abstract class AbstractProjection<A, B> implements Projection<A, B, L, R> {
    @Override public final Iterator<A> iterator() {
      return toOption().iterator();
    }

    @Override public final Either<L, R> either() {
      return Either.this;
    }

    @Override public final boolean isEmpty() {
      return !isDefined();
    }

    @Override public final Option<A> toOption() {
      return isDefined() ? some(get()) : none();
    }

    @Override public final Optional<A> toOptional() {
      return toOption().toOptional();
    }

    @Override public final boolean exists(final Predicate<? super A> f) {
      return isDefined() && f.test(get());
    }

    @Override final public A getOrNull() {
      return isDefined() ? get() : null;
    }

    @Override public final boolean forall(final Predicate<? super A> f) {
      return isEmpty() || f.test(get());
    }

    @Override public final A getOrError(final Supplier<String> err) {
      return toOption().getOrError(err);
    }

    @Override public <X extends Throwable> A getOrThrow(final Supplier<X> ifUndefined) throws X {
      return toOption().getOrThrow(ifUndefined);
    }

    @Override public final A getOr(final Supplier<? extends A> a) {
      return isDefined() ? get() : a.get();
    }

    @Deprecated @Override public final A getOrElse(final Supplier<? extends A> a) {
      return isDefined() ? get() : a.get();
    }

    @Override public final <X extends A> A getOrElse(final X x) {
      return isDefined() ? get() : x;
    }

    @Deprecated @Override public final void foreach(final Effect<? super A> f) {
      this.forEach(f::apply);
    }

    @Override public final void forEach(final Consumer<? super A> f) {
      if (isDefined()) {
        f.accept(get());
      }
    }
  }

  /**
   * A left projection of an either value.
   */
  public final class LeftProjection extends AbstractProjection<L, R> implements Projection<L, R, L, R> {
    private LeftProjection() {}

    public L get() {
      return getLeft();
    }

    @Override public boolean isDefined() {
      return isLeft();
    }

    public L on(final Function<? super R, ? extends L> f) {
      return isLeft() ? get() : f.apply(right().get());
    }

    //
    // definitions that can't be shared without higher-kinded types
    //

    /**
     * Map the given function across this projection's value if it has one.
     *
     * @param <X> the LHS type
     * @param f The function to map across this projection, must not return null
     * @return A new either value after mapping.
     */
    public <X> Either<X, R> map(final Function<? super L, X> f) {
      return isLeft() ? new Left<>(f.apply(get())) : this.<X> toRight();
    }

    /**
     * Binds the given function across this projection's value if it has one.
     *
     * @param <X> the LHS type
     * @param <RR> The existing RHS or a subtype
     * @param f The function to bind across this projection.
     * @return A new either value after binding.
     */
    public <X, RR extends R> Either<X, R> flatMap(final Function<? super L, Either<X, RR>> f) {
      if (isLeft()) {
        @SuppressWarnings("unchecked")
        final Either<X, R> result = (Either<X, R>) f.apply(get());
        return result;
      } else {
        return this.toRight();
      }
    }

    <X> Right<X, R> toRight() {
      return new Right<>(getRight());
    }

    /**
     * Anonymous bind through this projection.
     *
     * @param <X> the LHS type
     * @param e The value to bind with.
     * @return An either after binding through this projection.
     */
    public <X> Either<X, R> sequence(final Either<X, R> e) {
      return flatMap(Functions.<L, Either<X, R>> constant(e));
    }

    /**
     * Returns <code>None</code> if this projection has no value or if the given
     * predicate <code>p</code> does not hold for the value, otherwise, returns
     * a left in <code>Some</code>.
     *
     * @param <X> the RHS type
     * @param f The predicate function to test on this projection's value.
     * @return <code>None</code> if this projection has no value or if the given
     * predicate <code>p</code> does not hold for the value, otherwise, returns
     * a left in <code>Some</code>.
     */
    public <X> Option<Either<L, X>> filter(final Predicate<? super L> f) {
      if (isLeft() && f.test(get())) {
        final Either<L, X> result = new Left<>(get());
        return some(result);
      }
      return none();
    }

    /**
     * Function application on this projection's value.
     *
     * @param <X> the LHS type
     * @param either The either of the function to apply on this projection's
     * value.
     * @return The result of function application within either.
     *
     * @since 3.0
     */
    public <X> Either<X, R> ap(final Either<Function<L, X>, R> either) {
      return either.left().flatMap(this::map);
    }

    /**
     * Function application on this projection's value.
     *
     * @param <X> the LHS type
     * @param either The either of the function to apply on this projection's
     * value.
     * @return The result of function application within either.
     *
     * @deprecated since 3.0
     * @see #ap ap
     */
    @Deprecated public <X> Either<X, R> apply(final Either<Function<L, X>, R> either) {
      return ap(either);
    }

    /**
     * Coerces our right type as X. Dangerous, isLeft() must be true
     *
     * @param <X> the type to coerce to.
     * @return an either with the coerced right type.
     */
    <X> Either<L, X> as() {
      return left(get());
    }
  }

  /**
   * A right projection of an either value.
   */
  public final class RightProjection extends AbstractProjection<R, L> implements Projection<R, L, L, R> {
    private RightProjection() {}

    @Override public R get() {
      return getRight();
    }

    @Override public boolean isDefined() {
      return isRight();
    }

    @Override public R on(final Function<? super L, ? extends R> f) {
      return isRight() ? get() : f.apply(left().get());
    }

    //
    // definitions that can't be shared without higher-kinded types
    //

    /**
     * Map the given function across this projection's value if it has one.
     *
     * @param <X> the RHS type
     * @param f The function to map across this projection.
     * @return A new either value after mapping.
     */
    public <X> Either<L, X> map(final Function<? super R, X> f) {
      return isRight() ? new Right<>(f.apply(get())) : this.<X> toLeft();
    }

    /**
     * Binds the given function across this projection's value if it has one.
     *
     * @param <X> the RHS type
     * @param <LL> The existing LHS or a subtype
     * @param f The function to bind across this projection.
     * @return A new either value after binding.
     */
    public <X, LL extends L> Either<L, X> flatMap(final Function<? super R, Either<LL, X>> f) {
      if (isRight()) {
        @SuppressWarnings("unchecked")
        final Either<L, X> result = (Either<L, X>) f.apply(get());
        return result;
      } else {
        return this.toLeft();
      }
    }

    <X> Left<L, X> toLeft() {
      return new Left<>(left().get());
    }

    /**
     * Anonymous bind through this projection.
     *
     * @param <X> the RHS type
     * @param e The value to bind with.
     * @return An either after binding through this projection.
     */
    public <X> Either<L, X> sequence(final Either<L, X> e) {
      return flatMap(Functions.constant(e));
    }

    /**
     * Returns <code>None</code> if this projection has no value or if the given
     * predicate <code>p</code> does not hold for the value, otherwise, returns
     * a right in <code>Some</code>.
     *
     * @param <X> the LHS type
     * @param f The predicate function to test on this projection's value.
     * @return <code>None</code> if this projection has no value or if the given
     * predicate <code>p</code> does not hold for the value, otherwise, returns
     * a right in <code>Some</code>.
     */
    public <X> Option<Either<X, R>> filter(final Predicate<? super R> f) {
      if (isRight() && f.test(get())) {
        final Either<X, R> result = new Right<>(get());
        return some(result);
      }
      return none();
    }

    /**
     * Function application on this projection's value.
     *
     * @param <X> the RHS type
     * @param either The either of the function to apply on this projection's
     * value.
     * @return The result of function application within either.
     *
     * @since 3.0
     */
    public <X> Either<L, X> ap(final Either<L, Function<R, X>> either) {
      return either.right().flatMap(this::map);
    }

    /**
     * Function application on this projection's value.
     *
     * @param <X> the RHS type
     * @param either The either of the function to apply on this projection's
     * value.
     * @return The result of function application within either.
     *
     * @deprecated since 3.0 see ap
     */
    @Deprecated public <X> Either<L, X> apply(final Either<L, Function<R, X>> either) {
      return ap(either);
    }

    /**
     * Coerces our left type as X. Dangerous, isRight() must be true
     *
     * @param <X> the type to coerce to.
     * @return an either with the coerced left type.
     */
    <X> Either<X, R> as() {
      return right(get());
    }
  }

  public interface Projection<A, B, L, R> extends Maybe<A> {
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
    Option<? super A> toOption();

    /**
     * Returns this projection's value in {@link Optional#of} if it exists,
     * otherwise {@link Optional#empty()}.
     *
     * @return This projection's value in <code>of</code> if it exists,
     * otherwise <code>empty</code>.
     * @since 4.0
     */
    Optional<? super A> toOptional();

    /**
     * The value of this projection or the result of the given function on the
     * opposing projection's value.
     *
     * @param f The function to execute if this projection has no value.
     * @return The value of this projection or the result of the given function
     * on the opposing projection's value.
     */
    A on(Function<? super B, ? extends A> f);
  }
}
