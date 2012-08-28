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

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.MapMaker;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @since 1.1
 */
public class Functions {
  private Functions() {
    throw new UnsupportedOperationException();
  }

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

  static <F, T> Function<F, T> memoize(final Function<F, T> delegate, final MapMaker mapMaker) {
    return new Function<F, T>() {
      final Map<F, T> map = mapMaker.makeComputingMap(delegate);

      public T apply(final F from) {
        return map.get(from);
      }
    };
  }

  static final class Memoizer<F, T> implements Function<F, T> {
    final Map<F, T> map;

    Memoizer(final Function<F, T> delegate, final MapMaker mapMaker) {
      map = mapMaker.makeComputingMap(delegate);
    }

    public T apply(final F from) {
      return map.get(from);
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

  static <A, B, C> Function<A, Function<B, C>> curry(final Function2<A, B, C> f2) {
    return new Function<A, Function<B, C>>() {
      @Override
      public Function<B, C> apply(final A a) {
        return new Function<B, C>() {
          public C apply(B b) {
            return f2.apply(a, b);
          };
        };
      }
    };
  }

  /**
   * Function composition: one andThen two
   */
  static <A, B, C> Function<A, C> andThen(final Function<A, B> one, final Function<B, C> two) {
    return new Function<A, C>() {
      @Override
      public C apply(A from) {
        return two.apply(one.apply(from));
      }
    };
  }

  /**
   * Function composition. f compose g means apply g andThen f
   * 
   * @param f A function to compose with another.
   * @param g A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  static <A, B, C> Function<A, C> compose(final Function<B, C> f, final Function<A, B> g) {
    return andThen(g, f);
  }

  /**
   * Function composition.
   * 
   * @param f A function to compose with another.
   * @param g A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  static <A, B, C, D> Function<A, Function<B, D>> compose2(final Function<C, D> f, final Function<A, Function<B, C>> g) {
    return new Function<A, Function<B, D>>() {
      public Function<B, D> apply(final A a) {
        return new Function<B, D>() {
          public D apply(final B b) {
            return f.apply(g.apply(a).apply(b));
          }
        };
      }
    };
  }

  /**
   * Function argument flipping.
   * 
   * @return A function that takes a function and flips its arguments.
   */
  static <A, B, C> Function<Function<A, Function<B, C>>, Function<B, Function<A, C>>> flip() {
    return new Function<Function<A, Function<B, C>>, Function<B, Function<A, C>>>() {
      public Function<B, Function<A, C>> apply(final Function<A, Function<B, C>> f) {
        return flip(f);
      }
    };
  }

  /**
   * Function argument flipping.
   * 
   * @param f The function to flip.
   * @return The given function flipped.
   */
  static <A, B, C> Function<B, Function<A, C>> flip(final Function<A, Function<B, C>> f) {
    return new Function<B, Function<A, C>>() {
      public Function<A, C> apply(final B b) {
        return new Function<A, C>() {
          public C apply(final A a) {
            return f.apply(a).apply(b);
          }
        };
      }
    };
  }

  /**
   * Function argument flipping.
   * 
   * @param f The function to flip.
   * @return The given function flipped.
   */
  static <A, B, C> Function2<B, A, C> flip(final Function2<A, B, C> f) {
    return new Function2<B, A, C>() {
      public C apply(final B b, final A a) {
        return f.apply(a, b);
      }
    };
  }

  /**
   * Function argument flipping.
   * 
   * @return A function that flips the arguments of a given function.
   */
  static <A, B, C> Function<Function2<A, B, C>, Function2<B, A, C>> flip2() {
    return new Function<Function2<A, B, C>, Function2<B, A, C>>() {
      public Function2<B, A, C> apply(final Function2<A, B, C> f) {
        return flip(f);
      }
    };
  }
}