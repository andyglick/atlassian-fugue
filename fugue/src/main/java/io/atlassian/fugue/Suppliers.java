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

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provide utility functions for the class of functions that supply a return
 * value when invoked.
 *
 * @since 1.0
 */
public class Suppliers {
  /**
   * Creates a {@link java.util.function.Supplier} of a constant value.
   *
   * @param <A> the type
   * @param a the constant value to supply
   * @return a supplier that always supplies {@code instance}.
   */
  public static <A> Supplier<A> ofInstance(final A a) {
    return () -> a;
  }

  /**
   * Create a new {@link java.util.function.Supplier} by transforming the result
   * calling the first {@link java.util.function.Supplier}
   *
   * @param transform function to transform the result of a
   * {@link java.util.function.Supplier} of A's to B's
   * @param first a {@link java.util.function.Supplier} of A's
   * @param <A> return type of the {@link java.util.function.Supplier} to
   * transform
   * @param <B> return type of the new {@link java.util.function.Supplier}
   * @return a new {@link java.util.function.Supplier} returning B's
   */
  public static <A, B> Supplier<B> compose(final Function<? super A, B> transform, final Supplier<A> first) {
    return () -> transform.apply(first.get());
  }

  /**
   * Performs function application within a supplier (applicative functor
   * pattern).
   *
   * @param sa supplier
   * @param sf The Supplier function to apply.
   * @return A new Supplier after applying the given Supplier function to the
   * first argument.
   */
  public static <A, B> Supplier<B> ap(final Supplier<A> sa, final Supplier<Function<A, B>> sf) {
    return () -> sf.get().apply(sa.get());
  }

  /**
   * Supplies true.
   *
   * @return a supplier that always supplies {@code true}.
   */
  public static Supplier<Boolean> alwaysTrue() {
    return () -> true;
  }

  /**
   * Supplies false.
   *
   * @return a supplier that always supplies {@code false}.
   */
  public static Supplier<Boolean> alwaysFalse() {
    return () -> false;
  }

  /**
   * Always returns null. Not a very good idea.
   *
   * @param <A> the type
   * @return a supplier that always supplies {@code null}.
   */
  public static <A> Supplier<A> alwaysNull() {
    return () -> null;
  }

  /**
   * Turns an Option into a supplier, but throws an exception if undefined. Not
   * a very good idea.
   *
   * @param <A> the type
   * @param option the option to attempt to get values from
   * @return a {@link java.util.function.Supplier} that always calls
   * {@link io.atlassian.fugue.Option#get()}, which throws an Exception if the
   * option is None
   * @since 2.0
   */
  public static <A> Supplier<A> fromOption(final Option<A> option) {
    return option::get;
  }

  /**
   * Constantly applies the input value to the supplied function, and returns
   * the result.
   *
   * @param <A> the input type
   * @param <B> the result type
   * @param f the function
   * @param a the value
   * @return a {@link java.util.function.Supplier} that always calls
   * {@link java.util.function.Function#apply(Object)}
   * @since 2.2
   */
  public static <A, B> Supplier<B> fromFunction(final Function<? super A, ? extends B> f, final A a) {
    return () -> f.apply(a);
  }

  /**
   * A supplier that memoize the value return by another
   * {@link java.util.function.Supplier}, whose
   * {@link java.util.function.Supplier#get()} method is guaranteed to be call
   * at most once. The returned {@link java.util.function.Supplier} is
   * thread-safe
   *
   * @param <A> the type
   * @param supplier the supplier to memoize
   * @return the memoizing supplier
   */
  public static <A> Supplier<A> memoize(final Supplier<A> supplier) {
    return supplier instanceof MemoizingSupplier ? supplier : new MemoizingSupplier<>(Objects.requireNonNull(supplier));
  }

  /**
   * A supplier that weakly memoize the value return by another
   * {@link java.util.function.Supplier} , The returned
   * {@link java.util.function.Supplier} is thread-safe
   *
   * @param <A> the type
   * @param supplier the supplier to memoize
   * @return the weakly memoizing supplier
   */
  public static <A> Supplier<A> weakMemoize(final Supplier<A> supplier) {
    return supplier instanceof WeakMemoizingSupplier || supplier instanceof MemoizingSupplier ? supplier : new WeakMemoizingSupplier<>(
      Objects.requireNonNull(supplier));
  }

  private static final class MemoizingSupplier<A> implements Supplier<A> {

    private volatile Supplier<A> delegate;

    private A a;

    MemoizingSupplier(final Supplier<A> delegate) {
      this.delegate = delegate;
    }

    @Override public A get() {
      // double Checked Locking
      if (delegate != null) {
        synchronized (this) {
          if (delegate != null) {
            final A res;
            this.a = res = delegate.get();
            delegate = null;
            return res;
          }
        }
      }
      return a;
    }
  }

  private static final class WeakMemoizingSupplier<A> implements Supplier<A> {

    private final Supplier<A> delegate;

    // Contains a the value from delegate.
    private volatile WeakReference<A> value;

    WeakMemoizingSupplier(final Supplier<A> delegate) {
      this.delegate = delegate;
    }

    @Override public A get() {
      A a = value == null ? null : value.get();
      // double Checked Locking
      if (a == null) {
        synchronized (this) {
          a = value == null ? null : value.get();
          if (a == null) {
            a = delegate.get();
            value = new WeakReference<A>(a);
          }
        }
      }
      return a;
    }
  }
}
