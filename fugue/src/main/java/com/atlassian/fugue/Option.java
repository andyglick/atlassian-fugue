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
package com.atlassian.fugue;

import static com.atlassian.fugue.Suppliers.ofInstance;
import static com.atlassian.fugue.mango.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterators;
import com.atlassian.fugue.mango.Preconditions;

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
 * optionality, it can be {@link Functions#lift(Function) lifted} into a partial
 * function and then {@link #flatMap(Function) flat mapped} instead.
 * <p>
 * Note: while this class is public and abstract it does not expose a
 * constructor as only the concrete internal subclasses are designed to be used.
 * 
 * @param <A> the value type the option contains
 * 
 * @since 1.0
 */
public abstract class Option<A> implements Iterable<A>, Maybe<A>, Serializable {
  private static final long serialVersionUID = 7849097310208471377L;

  /**
   * Factory method for {@link Option} instances.
   * 
   * @param <A> the contained type
   * @param a the value to hold
   * @return a Some if the parameter is not null or a None if it is
   */
  public static <A> Option<A> option(final A a) {
    return (a == null) ? Option.<A> none() : some(a);
  }

  /**
   * Factory method for Some instances.
   * 
   * @param <A> the contained type
   * @param value the value to hold
   * @return a Some if the parameter is not null
   * @throws NullPointerException if the parameter is null
   */
  public static <A> Option<A> some(final A value) {
    Preconditions.checkNotNull(value);
    return new Some<A>(value);
  }

  /**
   * Factory method for None instances.
   * 
   * @param <A> the held type
   * @return a None
   */
  public static <A> Option<A> none() {
    @SuppressWarnings("unchecked")
    final Option<A> result = (Option<A>) NONE;
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
   * Function for wrapping values in a Some or None.
   * 
   * @param <A> the contained type
   * @return a {@link Function} to wrap values
   * 
   * @since 1.1
   */
  static <A> Function<A, Option<A>> toOption() {
    return new ToOption<A>();
  }

  /**
   * Predicate for filtering defined options only.
   * 
   * @param <A> the contained type
   * @return a {@link Predicate} that returns true only for defined options
   */
  public static <A> Predicate<Option<A>> defined() {
    return new Defined<A>();
  }

  /**
   * Supplies None as required. Useful as the zero value for folds.
   * 
   * @param <A> the contained type
   * @return a {@link Supplier} of None instances
   */
  public static <A> Supplier<Option<A>> noneSupplier() {
    return ofInstance(Option.<A> none());
  }

  /**
   * Find the first option that isDefined, or if there aren't any, then None.
   * 
   * @param <A> the contained type
   * @param options an Iterable of options to search through
   * @return the first option that isDefined, or none
   * 
   * @deprecated since 1.1 use {@link Options#find(Iterable)} instead
   */
  // /CLOVER:OFF
  @Deprecated public static <A> Option<A> find(final Iterable<Option<A>> options) {
    return Options.find(options);
  }

  /**
   * Filter out undefined options.
   * 
   * @param <A> the contained type
   * @param options many options that may or may not be defined
   * @return the filtered options
   * 
   * @deprecated since 1.1 use {@link Options#filterNone(Iterable)} instead
   */
  @Deprecated public static <A> Iterable<Option<A>> filterNone(final Iterable<Option<A>> options) {
    return Options.filterNone(options);
  }

  // /CLOVER:ON

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

  @Override public final <B extends A> A getOrElse(final B other) {
    return getOrElse(Suppliers.<A> ofInstance(other));
  }

  @Override public final A getOrElse(final Supplier<? extends A> supplier) {
    return fold(supplier, Functions.<A> identity());
  }

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
    return orElse(Suppliers.ofInstance(orElse));
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
    Option<A> result = (Option<A>) fold(orElse, Option.<A> toOption());
    return result;
  }

  @Override public final boolean exists(final Predicate<? super A> p) {
    checkNotNull(p);
    return isDefined() && p.apply(get());
  }

  @Override public boolean forall(final Predicate<? super A> p) {
    return isEmpty() || p.apply(get());
  }

  @Override public final boolean isEmpty() {
    return !isDefined();
  }

  @Override public final Iterator<A> iterator() {
    return fold(Suppliers.ofInstance(Iterators.<A> emptyIterator()), Functions.<A> singletonIterator());
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
    checkNotNull(f);
    return isEmpty() ? Option.<B> none() : new Some<B>(f.apply(get()));
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
    checkNotNull(f);
    @SuppressWarnings("unchecked")
    Option<B> result = (Option<B>) fold(Option.<B> noneSupplier(), f);
    return result;
  }

  /**
   * Returns this {@link Option} if it is nonempty <strong>and</strong> applying
   * the predicate to this option's value returns true. Otherwise, return
   * {@link #none()}.
   * 
   * @param p the predicate to test
   * @return this option, or none
   */
  public final Option<A> filter(final Predicate<? super A> p) {
    checkNotNull(p);
    return (isEmpty() || p.apply(get())) ? this : Option.<A> none();
  }

  /**
   * Creates an Either from this Option. Puts the contained value in a right if
   * {@link #isDefined()} otherwise puts the supplier's value in a left.
   * 
   * @param <X> the left type
   * @param left the Supplier to evaluate and return if this is empty
   * @return the content of this option if defined as a right, or the supplier's
   * content as a left if not
   * 
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
   * 
   * @see Option#toRight
   */
  public final <X> Either<A, X> toLeft(final Supplier<X> right) {
    return isEmpty() ? Either.<A, X> right(right.get()) : Either.<A, X> left(get());
  }

  @Override public final int hashCode() {
    return fold(NONE_HASH, SomeHashCode.instance());
  }

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

  @Override public final String toString() {
    return fold(NONE_STRING, SomeString.instance());
  }

  //
  // util methods
  //

  private Function<Object, Boolean> valuesEqual() {
    return new Function<Object, Boolean>() {
      public Boolean apply(final Object obj) {
        return isDefined() && get().equals(obj);
      }
    };
  }

  //
  // static members
  //

  private static final Option<Object> NONE = new Option<Object>() {
    private static final long serialVersionUID = -1978333494161467110L;

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

    @Override public <X extends Throwable> Object getOrThrow(Supplier<X> ifUndefined) throws X {
      throw ifUndefined.get();
    }

    @Override public void foreach(final Effect<? super Object> effect) {}
  };

  private static final Supplier<String> NONE_STRING = Suppliers.ofInstance("none()");
  private static final Supplier<Integer> NONE_HASH = Suppliers.ofInstance(31);

  static final class Defined<A> implements Predicate<Option<A>> {
    @Override public Boolean apply(final Option<A> option) {
      return option.isDefined();
    }
  };

  //
  // inner classes
  //

  /**
   * The big one, the actual implementation class.
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

    @Override public <X extends Throwable> A getOrThrow(Supplier<X> ifUndefined) throws X {
      return get();
    }

    @Override public void foreach(final Effect<? super A> effect) {
      effect.apply(value);
    }
  }

  enum SomeString implements Function<Object, String> {
    INSTANCE;

    public String apply(final Object obj) {
      return String.format("some(%s)", obj);
    }

    @SuppressWarnings("unchecked") static <A> Function<A, String> instance() {
      // Some IDEs reckon this doesn't compile. They are wrong. It compiles and
      // is correct.
      return (Function<A, String>) SomeString.INSTANCE;
    }
  }

  enum SomeHashCode implements Function<Object, Integer> {
    INSTANCE;

    public Integer apply(final Object a) {
      return a.hashCode();
    }

    @SuppressWarnings("unchecked") static <A> Function<A, Integer> instance() {
      // Some IDEs reckon this doesn't compile. They are wrong. It compiles and
      // is correct.
      return (Function<A, Integer>) SomeHashCode.INSTANCE;
    }
  }

  static class ToOption<A> implements Function<A, Option<A>> {
    @Override public Option<A> apply(final A a) {
      return option(a);
    }
  }
}
