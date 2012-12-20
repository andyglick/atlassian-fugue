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

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.copyOf;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

/**
 * @since 1.1
 */
public class Functions {
  private Functions() {}

  /**
   * Apply f to each element in elements, with each application using the result
   * of the previous application as the other argument to f. zero is used as the
   * first 'result' value. The final result is returned.
   * 
   * @param f the function to apply to all the elements
   * @param zero the starting point for the function
   * @param elements the series of which each element will be accumulated into a
   * result
   * 
   * @return the result of accumulating the application of f to all elements
   * 
   * @since 1.1
   */
  public static <F, T> T fold(final Function2<T, F, T> f, final T zero, final Iterable<F> elements) {
    T currentValue = zero;
    for (final F element : elements) {
      currentValue = f.apply(currentValue, element);
    }
    return currentValue;
  }

  /**
   * Apply f to each element in elements, with each application using the result
   * of the previous application as the other argument to f. zero is used as the
   * first 'result' value. The final result is returned.
   * 
   * @param f the function to apply to all elements
   * @param zero the starting point for the function
   * @param elements the series of which each element will be accumulated into a
   * result
   * 
   * @return the result of accumulating the application of f to all elements
   * 
   * @since 1.1
   */
  public static <F, T> T fold(final Function<Pair<T, F>, T> f, final T zero, final Iterable<F> elements) {
    return fold(new Function2<T, F, T>() {
      public T apply(final T arg1, final F arg2) {
        return f.apply(new Pair<T, F>(arg1, arg2));
      }
    }, zero, elements);
  }

  /**
   * Get the value from a supplier.
   * 
   * @param <T> the type returned, note the Supplier can be covariant.
   * @return a function that extracts the value from a supplier
   */
  static <T> Function<Supplier<? extends T>, T> fromSupplier() {
    return new ValueExtractor<T>();
  }

  private static class ValueExtractor<T> implements Function<Supplier<? extends T>, T> {
    public T apply(final Supplier<? extends T> supplier) {
      return supplier.get();
    }
  }

  /**
   * Function that takes another function and applies it to the argument.
   * 
   * @param <A> the argument and function input type
   * @param <B> the result type
   * @param arg the argument that will be applied to any input functions
   * @return a function that takes a function from A to B , applies the arg and
   * returns the result
   * 
   * @since 1.1
   */
  public static <A, B> Function<Function<A, B>, B> apply(final A arg) {
    return new Function<Function<A, B>, B>() {
      public B apply(final Function<A, B> f) {
        return f.apply(arg);
      }
    };
  }

  /**
   * PartialFunction that does a type check and matches if the value is of the
   * right type.
   * 
   * @param cls the type to check against.
   * @return a function that returns a Some with the value if of the right type
   * otherwise a None
   * @since 1.2
   */
  public static <A, B> Function<A, Option<B>> isInstanceOf(Class<B> cls) {
    return new InstanceOf<A, B>(cls);
  }

  static class InstanceOf<A, B> implements Function<A, Option<B>> {
    private final Class<B> cls;

    InstanceOf(Class<B> cls) {
      this.cls = checkNotNull(cls);
    }

    public Option<B> apply(A a) {
      return (cls.isAssignableFrom(a.getClass())) ? Option.some(cls.cast(a)) : Option.<B> none();
    }
  }

  /**
   * Create a PartialFunction from a {@link Predicate} and a {@link Function}.
   * 
   * @param p the predicate to test the value against.
   * @param f the function to apply if the predicate passes.
   * @return a PartialFunction that tests the supplied predicate before applying
   * the function.
   * @since 1.2
   */
  public static <A, B> Function<A, Option<B>> partial(Predicate<? super A> p, Function<? super A, ? extends B> f) {
    return new Partial<A, B>(p, f);
  }

  static class Partial<A, B> implements Function<A, Option<B>> {
    private final Predicate<? super A> p;
    private final Function<? super A, ? extends B> f;

    Partial(Predicate<? super A> p, Function<? super A, ? extends B> f) {
      this.p = checkNotNull(p);
      this.f = checkNotNull(f);
    }

    public Option<B> apply(A a) {
      return (p.apply(a)) ? Option.<B> option(f.apply(a)) : Option.<B> none();
    }
  }

  /**
   * Compose two PartialFunctions into one.
   * <p>
   * Kleisli composition. In Haskell it is defined as <code>&gt;=&gt;</code>,
   * AKA <a href="http://stackoverflow.com/a/7833488/210216">
   * "compose, fishy, compose"</a>
   * 
   * @param f the first partial function
   * @param g the second partial function
   * @return a PartialFunction that flatMaps g on to the result of applying f.
   * @since 1.2
   */
  public static <A, B, C> Function<A, Option<C>> compose(Function<? super A, Option<B>> f, Function<? super B, Option<C>> g) {
    return new PartialComposer<A, B, C>(f, g);
  }

  static class PartialComposer<A, B, C> implements Function<A, Option<C>> {
    private final Function<? super A, Option<B>> f;
    private final Function<? super B, Option<C>> g;

    PartialComposer(Function<? super A, Option<B>> f, Function<? super B, Option<C>> g) {
      this.f = checkNotNull(f);
      this.g = checkNotNull(g);
    }

    public Option<C> apply(A a) {
      return f.apply(a).flatMap(g);
    }
  }

  /**
   * Creates a stack of matcher functions and returns the first result that
   * matches.
   * 
   * @param f1 partial function, tried in order.
   * @param f2 partial function, tried in order.
   * @return a PartialFunction that composes all the functions and tries each
   * one in sequence.
   * @since 1.2
   */
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, Option<B>> f1, Function<? super A, Option<B>> f2) {
    @SuppressWarnings("unchecked")
    Matcher<A, B> result = matcher(f1, f2);
    return result;
  }

  /**
   * Creates a stack of matcher functions and returns the first result that
   * matches.
   * 
   * @param f1 partial function, tried in order.
   * @param f2 partial function, tried in order.
   * @param f3 partial function, tried in order.
   * @return a PartialFunction that composes all the functions and tries each
   * one in sequence.
   * @since 1.2
   */
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, Option<B>> f1, Function<? super A, Option<B>> f2,
    Function<? super A, Option<B>> f3) {
    @SuppressWarnings("unchecked")
    Matcher<A, B> result = matcher(f1, f2, f3);
    return result;
  }

  /**
   * Creates a stack of matcher functions and returns the first result that
   * matches.
   * 
   * @param f1 partial function, tried in order.
   * @param f2 partial function, tried in order.
   * @param f3 partial function, tried in order.
   * @param f4 partial function, tried in order.
   * @return a PartialFunction that composes all the functions and tries each
   * one in sequence.
   * @since 1.2
   */
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, Option<B>> f1, Function<? super A, Option<B>> f2,
    Function<? super A, Option<B>> f3, Function<? super A, Option<B>> f4) {
    @SuppressWarnings("unchecked")
    Matcher<A, B> result = matcher(f1, f2, f3, f4);
    return result;
  }

  /**
   * Creates a stack of matcher functions and returns the first result that
   * matches.
   * 
   * @param f1 partial function, tried in order.
   * @param f2 partial function, tried in order.
   * @param f3 partial function, tried in order.
   * @param f4 partial function, tried in order.
   * @param f5 partial function, tried in order.
   * @param fs partial functions, tried in order.
   * @return a PartialFunction that composes all the functions and tries each
   * one in sequence.
   * @since 1.2
   */
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, Option<B>> f1, Function<? super A, Option<B>> f2,
    Function<? super A, Option<B>> f3, Function<? super A, Option<B>> f4, Function<? super A, Option<B>> f5, Function<? super A, Option<B>>... fs) {
    Matcher<A, B> result = new Matcher<A, B>(com.google.common.collect.Iterables.concat(
      ImmutableList.<Function<? super A, Option<B>>> of(f1, f2, f3, f4, f5), copyOf(fs)));
    return result;
  }

  /* utility copy function */
  private static <A, B> Matcher<A, B> matcher(Function<? super A, Option<B>>... fs) {
    return new Matcher<A, B>(copyOf(fs));
  }

  static class Matcher<A, B> implements Function<A, Option<B>> {
    private final Iterable<Function<? super A, Option<B>>> fs;

    Matcher(Iterable<Function<? super A, Option<B>>> fs) {
      this.fs = checkNotNull(fs);
      checkState(!Iterables.isEmpty().apply(this.fs));
    }

    public Option<B> apply(A a) {
      for (Function<? super A, Option<B>> f : fs) {
        Option<B> b = f.apply(a);
        if (b.isDefined())
          return b;
      }
      return Option.none();
    }
  }

  /**
   * Function that can be used to ignore any RuntimeExceptions that a
   * {@link Supplier} may produce and return null instead.
   * 
   * @param <T> the result type
   * @return a Function that transforms an exception into a null
   */
  static <T> Function<Supplier<? extends T>, Supplier<T>> ignoreExceptions() {
    return new ExceptionIgnorer<T>();
  }

  static class ExceptionIgnorer<T> implements Function<Supplier<? extends T>, Supplier<T>> {
    public Supplier<T> apply(final Supplier<? extends T> from) {
      return new IgnoreAndReturnNull<T>(from);
    }
  }

  static class IgnoreAndReturnNull<T> implements Supplier<T> {
    private final Supplier<? extends T> delegate;

    IgnoreAndReturnNull(final Supplier<? extends T> delegate) {
      this.delegate = checkNotNull(delegate);
    }

    public T get() {
      try {
        return delegate.get();
      } catch (final RuntimeException ignore) {
        return null;
      }
    }
  }

  static <T> Function<T, List<T>> singletonList(final Class<T> c) {
    return new SingletonList<T>();
  }

  private static final class SingletonList<T> implements Function<T, List<T>> {
    public List<T> apply(final T o) {
      return ImmutableList.of(o);
    }
  }

  static Function<String, Either<NumberFormatException, Long>> parseLong() {
    return ParseLong.INSTANCE;
  }

  private enum ParseLong implements Function<String, Either<NumberFormatException, Long>> {
    INSTANCE;

    public Either<NumberFormatException, Long> apply(final String s) {
      try {
        return right(Long.valueOf(s));
      } catch (final NumberFormatException e) {
        return left(e);
      }
    }
  }

  static <A> Function<A, Iterator<A>> singletonIterator() {
    return new Function<A, Iterator<A>>() {
      public Iterator<A> apply(final A a) {
        return Iterators.singletonIterator(a);
      }
    };
  }

  static <A, X> Function<X, Iterator<A>> emptyIterator() {
    return new Function<X, Iterator<A>>() {
      public Iterator<A> apply(final X a) {
        return ImmutableList.<A> of().iterator();
      }
    };
  }

  static Function<Object, String> toStringFunction() {
    return com.google.common.base.Functions.toStringFunction();
  }

  static <A> Effect<A> toEffect(final Function<A, ?> function) {
    return new Effect<A>() {
      public void apply(final A a) {
        function.apply(a);
      }
    };
  }

  static Function<String, Either<NumberFormatException, Integer>> parseInt() {
    return ParseInt.INSTANCE;
  }

  private enum ParseInt implements Function<String, Either<NumberFormatException, Integer>> {
    INSTANCE;

    public Either<NumberFormatException, Integer> apply(final String s) {
      try {
        return right(Integer.valueOf(s));
      } catch (final NumberFormatException e) {
        return left(e);
      }
    }
  }

  static Function<String, Option<String>> trimToNone() {
    return TrimToNone.INSTANCE;
  }

  private enum TrimToNone implements Function<String, Option<String>> {
    INSTANCE;

    public Option<String> apply(final String s) {
      if (s == null) {
        return none();
      }
      final String trimmed = s.trim();
      return trimmed.isEmpty() ? Option.<String> none() : some(trimmed);
    }
  }

  static <T> Function<T, T> identity() {
    return com.google.common.base.Functions.identity();
  }

  static <A> Function<A, Option<A>> option() {
    return new ToOption<A>();
  }

  private static class ToOption<A> implements Function<A, Option<A>> {
    public Option<A> apply(final A from) {
      return Option.option(from);
    }
  }

  static <A, B> Function<A, B> constant(final B constant) {
    return new Function<A, B>() {
      public B apply(final A from) {
        return constant;
      }
    };
  }
}
