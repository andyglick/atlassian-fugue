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
   * Creates a {@link Supplier} of a constant value.
   * 
   * @param <A> the type
   * @param a the constant value to supply
   * @return a supplier that always supplies {@code instance}.
   */
  public static <A> Supplier<A> ofInstance(final A a) {
    return () -> a;
  }

  /**
   * Create a new {@link Supplier} by transforming the result calling the first
   * {@link Supplier}
   *
   * @param transform function to transform the result of a {@link Supplier} of
   * A's to B's
   * @param first a {@link Supplier} of A's
   * @param <A> return type of the {@link Supplier} to transform
   * @param <B> return type of the new {@link Supplier}
   * @return a new {@link Supplier} returning B's
   */
  public static <A, B> Supplier<B> compose(final Function<? super A, B> transform, final Supplier<A> first) {
    return () -> transform.apply(first.get());
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
   * @return a {@link Supplier} that always calls {@link Option#get()}, which
   * throws an Exception if the option is None
   * 
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
   * @return a {@link Supplier} that always calls {@link Function#apply(Object)}
   * 
   * @since 2.2
   */
  public static <A, B> Supplier<B> fromFunction(final Function<? super A, ? extends B> f, final A a) {
    return () -> f.apply(a);
  }
}
