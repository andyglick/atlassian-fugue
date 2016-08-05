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

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.atlassian.fugue.Suppliers.ofInstance;
import static java.util.Objects.requireNonNull;

/**
 * A class that encapsulates missing values. An Option may be either
 * <em>some</em> value or <em>none</em>.
 * <p>
 * If it is a value it may be tested with the {@link #isDefined()} method, but
 * more often it is useful to either return the value or an alternative if
 * {@link #getOrElse(Object) not set}, or {@link #map(Function) map} or
 * {@link #filter(Predicate) filter}.
 * <p>
 * Mapping a <em>none</em> of type A to type B will simply return a none of type
 * B if performed on a none of type A. Similarly, filtering will always fail on
 * a <em>none</em>.
 * <p>
 * This class is often used as an alternative to <code>null</code> where
 * <code>null</code> may be used to represent an optional value. There are
 * however some situations where <code>null</code> may be a legitimate value,
 * and that even though the option is defined, it still carries a
 * <code>null</code> inside. Specifically, this will happen if a function is
 * mapped across it returns null, as it is necessary to preserve the <a
 * href=http://en.wikipedia.org/wiki/Functor>Functor composition law</a>. Note
 * however, that this should be rare as functions that return <code>null</code>
 * is a bad idea anyway. <b>Note</b> that if a function returns null to indicate
 * optionality, it can be {@link io.atlassian.fugue.Options#lift(Function)
 * lifted} into a partial function and then {@link #flatMap(Function) flat
 * mapped} instead.
 * <p>
 * Note: while this class is public and abstract it does not expose a
 * constructor as only the concrete internal subclasses are designed to be used.
 *
 * @param <A> the value type the option contains
 * @since 1.0
 */
public abstract class Option<A> implements Iterable<A>, Maybe<A>, Serializable {
  private static final long serialVersionUID = 7849097310208471377L;

  /**
   * Factory method for {@link io.atlassian.fugue.Option} instances.
   *
   * @param <A> the contained type
   * @param a the value to hold
   * @return a Some if the parameter is not null or a None if it is
   */
  public static <A> Option<A> option(final A a) {
    return (a == null) ? Option.<A> none() : new Some<>(a);
  }

  /**
   * Factory method for Some instances.
   *
   * @param <A> the contained type
   * @param value the value to hold, must not be null
   * @return a Some if the parameter is not null
   * @throws java.lang.NullPointerException if the parameter is null
   */
  public static <A> Option<A> some(final A value) {
    requireNonNull(value);
    return new Some<>(value);
  }

  /**
   * Factory method for None instances.
   *
   * @param <A> the held type
   * @return a None
   */
  public static <A> Option<A> none() {
    @SuppressWarnings("unchecked")
    final Option<A> result = (Option<A>) None.NONE;
    return result;
  }

  /**
   * Factory method for None instances where the type token is handy. Allows
   * calling in-line where the type inferencer would otherwise complain.
   *
   * @param <A> the contained type
   * @param type token of the right type, unused, only here for the type
   * inferencer
   * @return a None
   */
  public static <A> Option<A> none(final Class<A> type) {
    return none();
  }

  /**
   * Predicate for filtering defined options only.
   *
   * @param <A> the contained type
   * @return a {@link java.util.function.Predicate} that returns true only for
   * defined options
   */
  public static <A> Predicate<Option<A>> defined() {
    return Maybe::isDefined;
  }

  /**
   * Supplies None as required. Useful as the zero value for folds.
   *
   * @param <A> the contained type
   * @return a {@link java.util.function.Supplier} of None instances
   */
  public static <A> Supplier<Option<A>> noneSupplier() {
    return ofInstance(Option.<A> none());
  }

  /**
   * Factory method for {@link io.atlassian.fugue.Option} instances from
   * {@link java.util.Optional} instances.
   *
   * @param <A> the contained type
   * @return a Some if {@link java.util.Optional#isPresent()} or a None
   * otherwise.
   * @param optional a {@link java.util.Optional} object.
   */
  public static <A> Option<A> fromOptional(final Optional<A> optional) {
    return option(optional.orElse(null));
  }

  //
  // ctors
  //

  /** do not constructor */
  Option() {}

  //
  // abstract methods
  //

  /**
   * If this is a some value apply the some function, otherwise get the None
   * value.
   *
   * @param <B> the result type
   * @param none the supplier of the None type
   * @param some the function to apply if this is a Some
   * @return the appropriate value
   */
  public abstract <B> B fold(Supplier<? extends B> none, Function<? super A, ? extends B> some);

  //
  // implementing Maybe
  //

  /** {@inheritDoc} */
  @Override public final <B extends A> A getOrElse(final B other) {
    return getOr(Suppliers.<A> ofInstance(other));
  }

  /** {@inheritDoc} */
  @Override public final A getOr(final Supplier<? extends A> supplier) {
    return fold(supplier, Functions.<A> identity());
  }

  /** {@inheritDoc} */
  @Deprecated @Override public final A getOrElse(final Supplier<? extends A> supplier) {
    return fold(supplier, Functions.<A> identity());
  }

  /** {@inheritDoc} */
  @Override public final A getOrNull() {
    return fold(Suppliers.<A> alwaysNull(), Functions.<A> identity());
  }

  /**
   * If this is a some, return the same some. Otherwise, return {@code orElse}.
   *
   * @param orElse option to return if this is none
   * @return this or {@code orElse}
   * @since 1.1
   */
  public final Option<A> orElse(final Option<? extends A> orElse) {
    return orElse(ofInstance(orElse));
  }

  /**
   * If this is a some, return the same some. Otherwise, return value supplied
   * by {@code orElse}.
   *
   * @param orElse supplier which provides the option to return if this is none
   * @return this or value supplied by {@code orElse}
   * @since 1.1
   */
  public final Option<A> orElse(final Supplier<? extends Option<? extends A>> orElse) {
    @SuppressWarnings("unchecked")
    // safe covariant cast
    final Option<A> result = (Option<A>) fold(orElse, Options.toOption());
    return result;
  }

  /** {@inheritDoc} */
  @Override public final boolean exists(final Predicate<? super A> p) {
    requireNonNull(p);
    return isDefined() && p.test(get());
  }

  /** {@inheritDoc} */
  @Override public final boolean forall(final Predicate<? super A> p) {
    requireNonNull(p);
    return isEmpty() || p.test(get());
  }

  /** {@inheritDoc} */
  @Override public final boolean isEmpty() {
    return !isDefined();
  }

  /** {@inheritDoc} */
  @Override public final Iterator<A> iterator() {
    return fold(ofInstance(Iterators.<A> emptyIterator()), Iterators::<A> singletonIterator);
  }

  //
  // stuff that can't be put on an interface without HKT
  //

  /**
   * Apply {@code f} to the value if defined.
   * <p>
   * Transforms to an option of the result type.
   *
   * @param <B> return type of {@code f}
   * @param f function to apply to wrapped value
   * @return new wrapped value
   */
  public final <B> Option<B> map(final Function<? super A, ? extends B> f) {
    requireNonNull(f);
    return isEmpty() ? Option.<B> none() : new Some<>(f.apply(get()));
  }

  /**
   * Apply {@code f} to the value if defined.
   * <p>
   * Transforms to an option of the result type.
   *
   * @param <B> return type of {@code f}
   * @param f function to apply to wrapped value
   * @return value returned from {@code f}
   */
  public final <B> Option<B> flatMap(final Function<? super A, ? extends Option<? extends B>> f) {
    requireNonNull(f);
    @SuppressWarnings("unchecked")
    final Option<B> result = (Option<B>) fold(Option.<B> noneSupplier(), f);
    return result;
  }

  /**
   * Returns this {@link io.atlassian.fugue.Option} if it is nonempty
   * <strong>and</strong> applying the predicate to this option's value returns
   * true. Otherwise, return {@link #none()}.
   *
   * @param p the predicate to test
   * @return this option, or none
   */
  public final Option<A> filter(final Predicate<? super A> p) {
    requireNonNull(p);
    return (isEmpty() || p.test(get())) ? this : Option.<A> none();
  }

  /**
   * Creates an Either from this Option. Puts the contained value in a right if
   * {@link #isDefined()} otherwise puts the supplier's value in a left.
   *
   * @param <X> the left type
   * @param left the Supplier to evaluate and return if this is empty
   * @return the content of this option if defined as a right, or the supplier's
   * content as a left if not
   * @see Option#toLeft
   */
  public final <X> Either<X, A> toRight(final Supplier<X> left) {
    return isEmpty() ? Either.<X, A> left(left.get()) : Either.<X, A> right(get());
  }

  /**
   * Creates an Either from this Option. Puts the contained value in a left if
   * {@link #isDefined()} otherwise puts the supplier's value in a right.
   *
   * @param <X> the right type
   * @param right the Supplier to evaluate and return if this is empty
   * @return the content of this option if defined as a left, or the supplier's
   * content as a right if not defined.
   * @see Option#toRight
   */
  public final <X> Either<A, X> toLeft(final Supplier<X> right) {
    return isEmpty() ? Either.<A, X> right(right.get()) : Either.<A, X> left(get());
  }

  /**
   * Create an {@link java.util.Optional} from this option. Will throw a
   * {@link java.lang.NullPointerException} if this option is defined and
   * contains a null value.
   *
   * @return {@link java.util.Optional#of(Object)} with the value if defined and
   * not null, {@link java.util.Optional#empty()} if not defined.
   */
  public abstract Optional<A> toOptional();

  /** {@inheritDoc} */
  @Override public final int hashCode() {
    return fold(NONE_HASH, SOME_HASH);
  }

  /** {@inheritDoc} */
  @Override public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || !(obj instanceof Option<?>)) {
      return false;
    }
    final Option<?> other = (Option<?>) obj;
    return other.fold(isDefined() ? Suppliers.alwaysFalse() : Suppliers.alwaysTrue(), valuesEqual());
  }

  /** {@inheritDoc} */
  @Override public final String toString() {
    return fold(NONE_STRING, SOME_STRING);
  }

  //
  // util methods
  //

  private Function<Object, Boolean> valuesEqual() {
    return obj -> isDefined() && Objects.equals(get(), obj);
  }

  //
  // static members
  //

  private static final Supplier<String> NONE_STRING = ofInstance("none()");
  private static final Supplier<Integer> NONE_HASH = ofInstance(31);

  private static final Function<Object, String> SOME_STRING = obj -> String.format("some(%s)", obj);
  private static final Function<Object, Integer> SOME_HASH = Object::hashCode;

  //
  // inner classes
  //

  /**
   * One of the big two, the actual implementation classes.
   * @since 4.1.0
   */
  static final class None extends Option<Object> {
    private static final long serialVersionUID = -1978333494161467110L;

    private static final Option<Object> NONE = new None();

    @Override public <B> B fold(final Supplier<? extends B> none, final Function<? super Object, ? extends B> some) {
      return none.get();
    }

    @Override public Object get() {
      throw new NoSuchElementException();
    }

    @Override public boolean isDefined() {
      return false;
    }

    @Override public Object getOrError(final Supplier<String> err) {
      throw new AssertionError(err.get());
    }

    @Override public <X extends Throwable> Object getOrThrow(final Supplier<X> ifUndefined) throws X {
      throw ifUndefined.get();
    }

    @Deprecated @Override public void foreach(final Effect<? super Object> effect) {
      this.forEach(effect);
    }

    @Override public void forEach(final Consumer<? super Object> effect) {}

    @Override public Optional<Object> toOptional() {
      return Optional.empty();
    }

    private Object readResolve() {
      return None.NONE;
    }
  }

  /**
   * One of the big one, the actual implementation classes.
   */
  static final class Some<A> extends Option<A> {
    private static final long serialVersionUID = 5542513144209030852L;

    private final A value;

    private Some(final A value) {
      this.value = value;
    }

    @Override public <B> B fold(final Supplier<? extends B> none, final Function<? super A, ? extends B> f) {
      return f.apply(value);
    }

    @Override public A get() {
      return value;
    }

    @Override public boolean isDefined() {
      return true;
    }

    @Override public A getOrError(final Supplier<String> err) {
      return get();
    }

    @Override public <X extends Throwable> A getOrThrow(final Supplier<X> ifUndefined) throws X {
      return get();
    }

    @Deprecated @Override public void foreach(final Effect<? super A> effect) {
      this.forEach(effect);
    }

    @Override public void forEach(final Consumer<? super A> effect) {
      effect.accept(value);
    }

    @Override public Optional<A> toOptional() {
      return Optional.of(value);
    }
  }

  /**
   * Backwards compatibility requires us to have a class Option$1 so we can
   * deserialize it into Option$None.
   */
  @Deprecated private static final Serializable NONE = new Serializable() {
    private static final long serialVersionUID = -1978333494161467110L;

    private Object readResolve() {
      return None.NONE;
    }
  };

}
