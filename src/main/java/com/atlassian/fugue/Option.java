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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.atlassian.fugue.Either.Left;
import com.atlassian.fugue.Either.Right;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterators;

/**
 * A class that encapsulates missing values. An Option may be either
 * {@link Option.Some some} value or {@link Option.None none}.
 * <p>
 * If it is a value it may be tested with the {@link #isDefined()} method, but
 * more often it is useful to either return the value or an alternative if
 * {@link #getOrElse(Object) not set}, or {@link #map(Function) map} or
 * {@link #filter(Predicate) filter}.
 * <p>
 * Mapping a {@link Option.None none} of type A to type B will simply return
 * {@link Option.None none} of type B if performed on a none of type A.
 * Similarly, filtering will always fail on a {@link Option.None none}.
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
 * While this class is public and abstract it does not expose a constructor as
 * only the concrete {@link Option.Some Some} and {@link Option.None None}
 * subclasses are meant to be used.
 * 
 * @param <A> the value type the option contains
 * 
 * @since 1.0
 */
public abstract class Option<A> implements Iterable<A>, Supplier<A>, Maybe<A> {
  /**
   * Factory method for {@link Option} instances.
   * 
   * @param <A> the contained type
   * @param a the value to hold
   * @return a {@link Option.Some Some} if the parameter is not null or a
   * {@link Option.None none} if it is
   */
  public static <A> Option<A> option(final A a) {
    return (a == null) ? Option.<A> none() : some(a);
  }

  /**
   * Factory method for {@link Option.Some Some} instances.
   * 
   * @param <A> the contained type
   * @param value the value to hold
   * @return a {@link Option.Some Some} if the parameter is not null
   * @throws NullPointerException if the parameter is null
   */
  public static <A> Option<A> some(final A value) {
    Preconditions.checkNotNull(value);
    return new Some<A>(value);
  }

  /**
   * Factory method for {@link Option.None none} instances.
   * 
   * @param <A> the held type
   * @return a {@link Option.None none}
   */
  public static <A> Option<A> none() {
    @SuppressWarnings("unchecked")
    final Option<A> result = (Option<A>) NONE;
    return result;
  }

  /**
   * Factory method for {@link Option.None none} instances where the type token
   * is handy. Allows calling in-line where the type inferencer would otherwise
   * complain.
   * 
   * @param <A> the contained type
   * @param type token of the right type, unused, only here for the type
   * inferencer
   * @return a {@link Option.None none}
   */
  public static <A> Option<A> none(final Class<A> type) {
    return none();
  }

  /**
   * Function for wrapping values in a {@link Option.Some some} or
   * {@link Option.None none}.
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
  public static <A> Predicate<? super A> defined() {
    @SuppressWarnings("unchecked")
    final Predicate<A> result = (Predicate<A>) DEFINED;
    return result;
  }

  /**
   * Supplies {@link Option.None none} as required. Useful as the zero value for
   * folds.
   * 
   * @param <A> the contained type
   * @return a {@link Supplier} of {@link Option.None none} instances
   */
  public static <A> Supplier<Option<A>> noneSupplier() {
    return ofInstance(Option.<A> none());
  }

  /**
   * Find the first option that isDefined, or if there aren't any, then
   * {@link Option.None none}.
   * 
   * @param <A> the contained type
   * @param options an Iterable of options to search through
   * 
   * @deprecated since 1.1 use {@link Options#find(Iterable)} instead
   */
  // /CLOVER:OFF
  @Deprecated public static <A> Option<A> find(final Iterable<Option<A>> options) {
    return Options.find(options);
  }

  // /CLOVER:ON

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

  //
  // ctors
  //

  /** do not constructor */
  Option() {}

  //
  // abstract methods
  //

  /**
   * If this is a some value apply the some function, otherwise get the
   * {@link Option.None none} value.
   * 
   * @param <B> the result type
   * @param none the supplier of the None type
   * @param some the function to apply if this is a {@link Option.Some Some}
   * @return the appropriate value
   */
  public abstract <B> B fold(Supplier<? extends B> none, Function<? super A, ? extends B> some);

  //
  // implementing Maybe
  //

  @Override public final <B extends A> A getOrElse(final B other) {
    return getOrElse(Suppliers.<A> ofInstance(other));
  }

  @Override public final A getOrElse(final Supplier<A> supplier) {
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
  public final Option<A> orElse(final Option<A> orElse) {
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
  public final Option<A> orElse(final Supplier<Option<A>> orElse) {
    return fold(orElse, Option.<A> toOption());
  }

  @Override public final boolean exists(final Predicate<A> p) {
    checkNotNull(p);
    return isDefined() && p.apply(get());
  }

  @Override public boolean forall(final Predicate<A> p) {
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
   * Transforms to an option of the retry result type.
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
   * Transforms to an option of the retry result type.
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
   */
  public final Option<A> filter(final Predicate<? super A> p) {
    checkNotNull(p);
    return (isEmpty() || p.apply(get())) ? this : Option.<A> none();
  }

  /**
   * @return a {@link Left} containing the given supplier's value if this is
   * empty, or a {@link Right} containing this option's value if this option is
   * defined.
   * @param left the Supplier to evaluate and return if this is empty
   * @see toLeft
   */
  public final <X> Either<X, A> toRight(final Supplier<X> left) {
    return isEmpty() ? Either.<X, A> left(left.get()) : Either.<X, A> right(get());
  }

  /**
   * @return a {@link Right} containing the given supplier's value if this is
   * empty, or a {@link Left} containing this option's value if this option is
   * defined.
   * @param right the Supplier to evaluate and return if this is empty
   * @see toLeft
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

    @Override public void foreach(final Effect<Object> effect) {}
  };

  private static final Supplier<String> NONE_STRING = Suppliers.ofInstance("none()");
  private static final Supplier<Integer> NONE_HASH = Suppliers.ofInstance(31);

  static final Predicate<Option<?>> DEFINED = new Predicate<Option<?>>() {
    @Override public boolean apply(final Option<?> option) {
      return option.isDefined();
    }
  };

  //
  // inner classes
  //

  /**
   * The big one, the actual implementation class.
   */
  private static final class Some<A> extends Option<A> {
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

    @Override public void foreach(final Effect<A> effect) {
      effect.apply(value);
    }
  }

  private enum SomeString implements Function<Object, String> {
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

  private enum SomeHashCode implements Function<Object, Integer> {
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

  private static class ToOption<A> implements Function<A, Option<A>> {
    @Override public Option<A> apply(final A a) {
      return option(a);
    }
  }
}
