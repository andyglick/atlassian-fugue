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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.copyOf;

import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

/**
 * Utility methods for Functions that are in addition to the methods on
 * {@link com.google.common.base.Functions}.
 * <P>
 * Note that this class defines Partial Functions to be functions that return an
 * {@link Option} of the result type, and has some methods for creating them.
 * 
 * @since 1.1
 */
public class Functions {

  // /CLOVER:OFF

  private Functions() {}

  // /CLOVER:ON

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
   * Partial Function that does a type check and matches if the value is of the
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
   * @param bc partial function from {@code B -> C}
   * @param ab partial function from {@code A -> B}
   * @return a PartialFunction that flatMaps g on to the result of applying f.
   * @since 1.2
   */
  public static <A, B, C> Function<A, Option<C>> compose(Function<? super B, ? extends Option<? extends C>> bc,
    Function<? super A, ? extends Option<? extends B>> ab) {
    return new PartialComposer<A, B, C>(ab, bc);
  }

  static class PartialComposer<A, B, C> implements Function<A, Option<C>> {
    private final Function<? super A, ? extends Option<? extends B>> ab;
    private final Function<? super B, ? extends Option<? extends C>> bc;

    PartialComposer(Function<? super A, ? extends Option<? extends B>> ab, Function<? super B, ? extends Option<? extends C>> bc) {
      this.ab = checkNotNull(ab);
      this.bc = checkNotNull(bc);
    }

    public Option<C> apply(A a) {
      return ab.apply(a).flatMap(bc);
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
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1,
    Function<? super A, ? extends Option<? extends B>> f2) {
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
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1,
    Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3) {
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
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1,
    Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3,
    Function<? super A, ? extends Option<? extends B>> f4) {
    Matcher<A, B> result = new Matcher<A, B>(ImmutableList.<Function<? super A, ? extends Option<? extends B>>> of(f1, f2, f3, f4));
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
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1,
    Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3,
    Function<? super A, ? extends Option<? extends B>> f4, Function<? super A, ? extends Option<? extends B>> f5,
    Function<? super A, ? extends Option<? extends B>>... fs) {
    Matcher<A, B> result = new Matcher<A, B>(com.google.common.collect.Iterables.concat(
      ImmutableList.<Function<? super A, ? extends Option<? extends B>>> of(f1, f2, f3, f4, f5), copyOf(fs)));
    return result;
  }

  /* utility copy function */
  private static <A, B> Matcher<A, B> matcher(Function<? super A, ? extends Option<? extends B>>... fs) {
    return new Matcher<A, B>(copyOf(fs));
  }

  static class Matcher<A, B> implements Function<A, Option<B>> {
    private final Iterable<Function<? super A, ? extends Option<? extends B>>> fs;

    Matcher(Iterable<Function<? super A, ? extends Option<? extends B>>> fs) {
      this.fs = checkNotNull(fs);
      checkState(!Iterables.isEmpty().apply(this.fs));
    }

    public Option<B> apply(A a) {
      for (Function<? super A, ? extends Option<? extends B>> f : fs) {
        @SuppressWarnings("unchecked")
        Option<B> b = (Option<B>) f.apply(a);
        if (b.isDefined())
          return b;
      }
      return Option.none();
    }
  }

  /**
   * Maps a function that returns nulls into a Partial function that returns an
   * Option of the result.
   * 
   * @param f the function that may return nulls
   * @return a function that converts any nulls into Options
   * @since 1.3
   */
  public static <A, B> Function<A, Option<B>> mapNullToOption(Function<? super A, ? extends B> f) {
    return new MapNullToOption<A, B>(f);
  }

  /**
   * @deprecated this is a poor name, use {@link #mapNullToOption(Function)}
   * instead
   * @since 1.2
   */
  // TODO deprecated in 1.3, remove in >= 1.5

  // /CLOVER:OFF

  @Deprecated public static <A, B> Function<A, Option<B>> lift(Function<? super A, ? extends B> f) {
    return mapNullToOption(f);
  }

  // /CLOVER:ON

  static class MapNullToOption<A, B> implements Function<A, Option<B>> {
    private final Function<? super A, ? extends B> f;

    MapNullToOption(Function<? super A, ? extends B> f) {
      this.f = f;
    }

    @Override public Option<B> apply(A a) {
      return Option.<B> option(f.apply(a));
    }
  }

  static <A> Function<A, Iterator<A>> singletonIterator() {
    return new Function<A, Iterator<A>>() {
      public Iterator<A> apply(final A a) {
        return Iterators.singletonIterator(a);
      }
    };
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
