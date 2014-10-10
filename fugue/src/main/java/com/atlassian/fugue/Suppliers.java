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

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Library of utility {@link Supplier} functions not provided by
 * {@link com.google.common.base.Suppliers}.
 * 
 * @since 1.0
 */
public class Suppliers {
  /**
   * Creates a {@link Supplier} of a constant value.
   * 
   * @param <A> the type
   * @param instance the constant value to supply
   * @return a supplier that always supplies {@code instance}.
   */
  public static <A> Supplier<A> ofInstance(final A instance) {
    return com.google.common.base.Suppliers.ofInstance(instance);
  }

  /**
   * Supplies true.
   * 
   * @return a supplier that always supplies {@code true}.
   */
  public static Supplier<Boolean> alwaysTrue() {
    return SupplyTrue.INSTANCE;
  }

  /**
   * Supplies false.
   * 
   * @return a supplier that always supplies {@code false}.
   */
  public static Supplier<Boolean> alwaysFalse() {
    return SupplyFalse.INSTANCE;
  }

  /**
   * Always returns null. Not a very good idea.
   * 
   * @param <A> the type
   * @return a supplier that always supplies {@code null}.
   */
  public static <A> Supplier<A> alwaysNull() {
    @SuppressWarnings("unchecked")
    final Supplier<A> result = (Supplier<A>) Nulls.NULL;
    return result;
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
    return new Supplier<A>() {
      @Override public A get() {
        return option.get();
      }
    };
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
    return new Supplier<B>() {
      @Override public B get() {
        return f.apply(a);
      }
    };
  }

  private enum SupplyTrue implements Supplier<Boolean> {
    INSTANCE;

    public Boolean get() {
      return true;
    }
  }

  private enum SupplyFalse implements Supplier<Boolean> {
    INSTANCE;

    public Boolean get() {
      return false;
    }
  }

  enum Nulls implements Supplier<Object> {
    NULL;

    public Object get() {
      return null;
    }
  }
}
