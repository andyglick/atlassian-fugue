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

import com.google.common.base.Supplier;

/**
 * Library of utility {@link Supplier} functions not provided by
 * {@link com.google.common.base.Suppliers}.
 * 
 * @since 1.0
 */
public class Suppliers {
  /**
   * @return a supplier that always supplies {@code instance}.
   */
  public static <T> Supplier<T> ofInstance(final T instance) {
    return com.google.common.base.Suppliers.ofInstance(instance);
  }

  /**
   * @return a supplier that always supplies {@code true}.
   */
  public static Supplier<Boolean> alwaysTrue() {
    return SupplyTrue.INSTANCE;
  }

  /**
   * @return a supplier that always supplies {@code false}.
   */
  public static Supplier<Boolean> alwaysFalse() {
    return SupplyFalse.INSTANCE;
  }

  /**
   * @return a supplier that always supplies {@code null}.
   */
  public static <A> Supplier<A> alwaysNull() {
    @SuppressWarnings("unchecked")
    final Supplier<A> result = (Supplier<A>) Nulls.NULL;
    return result;
  }

  /**
   * @return a {@link Supplier} that always calls {@link Option#get()}, which
   * throws an Exception if the option is None
   * 
   * @since 1.3
   */
  public static <A> Supplier<A> fromOption(final Option<A> option) {
    return new Supplier<A>() {
      @Override public A get() {
        return option.get();
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
