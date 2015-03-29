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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import static com.google.common.base.Throwables.propagateIfPossible;
import static java.util.Objects.requireNonNull;

/**
 * Static utility methods pertaining to instances of {@link Throwable} not
 * provided by Guava.
 * 
 * @since 1.2
 */
public final class Throwables {
  /**
   * Propagates {@code throwable} as-is if it is an instance of
   * {@link RuntimeException} or {@link Error}, or else as a last resort, wraps
   * it in a {@code RuntimeException} provided by the function and then
   * propagates.
   * <p>
   * This method always throws an exception. The {@code RuntimeException} return
   * type is only for client code to make Java type system happy in case a
   * return value is required by the enclosing method. Example usage:
   * 
   * <pre>
   * T doSomething() {
   *   try {
   *     return someMethodThatCouldThrowAnything();
   *   } catch (IKnowWhatToDoWithThisException e) {
   *     return handle(e);
   *   } catch (Throwable t) {
   *     throw Throwables.propagate(t, new Function&lt;MyRuntimeException&gt;() {
   *       public MyRuntimeException apply(Throwable t) {
   *         return new MyRuntimeException(t);
   *       }
   *     });
   *   }
   * }
   * </pre>
   * 
   * @param <R> the type of the Throwable to propagate, must be a RuntimeException
   * @param throwable the Throwable to propagate
   * @param function the function to transform the throwable into a runtime
   * exception
   * @return nothing will ever be returned; this return type is only for your
   * convenience, as illustrated in the example above
   */
  public static <R extends RuntimeException> R propagate(Throwable throwable, Function<Throwable, R> function) {
    propagateIfPossible(requireNonNull(throwable));
    throw function.apply(throwable);
  }

  /**
   * Propagates {@code throwable} as-is if it is an instance of
   * {@link RuntimeException} or {@link Error}, or else as a last resort, wraps
   * it in the {@code RuntimeException} specified by the {@code runtimeType}
   * parameter provided and then propagates.
   * <p>
   * This method always throws an exception. The {@code RuntimeException} return
   * type is only for client code to make Java type system happy in case a
   * return value is required by the enclosing method.
   * <p>
   * The runtime type passed as a parameter must be a runtime exception with a
   * constructor taking a single {@code Throwable} as an argument accessible via
   * reflection. If this is not the case an appropriate exception (
   * {@code NoSuchMethodException}, {@code InstantiationException},
   * {@code IllegalAccessException}, {@code InvocationTargetException}) will be
   * thrown wrapped in a simple {@code RuntimeException}. If you can't make your
   * exception match those criteria, you might want to look at using
   * {@link #propagate(Throwable, Function)}.
   * <p>
   * 
   * Example usage:
   * 
   * <pre>
   * T doSomething() {
   *   try {
   *     return someMethodThatCouldThrowAnything();
   *   } catch (IKnowWhatToDoWithThisException e) {
   *     return handle(e);
   *   } catch (Throwable t) {
   *     throw Throwables.propagate(t, MyRuntimeException.class);
   *   }
   * }
   * </pre>
   * 
   * @param <R> the type of the Throwable to propagate, must be a RuntimeException
   * @param throwable the Throwable to propagate
   * @param runtimeType the type of exception to use.
   * @return nothing will ever be returned; this return type is only for your
   * convenience, as illustrated in the example above
   * @see #propagate(Throwable, Function)
   */
  public static <R extends RuntimeException> R propagate(Throwable throwable, Class<R> runtimeType) {
    return propagate(throwable, new ExceptionFunction<R>(requireNonNull(runtimeType)));
  }

  private final static class ExceptionFunction<E extends Exception> implements Function<Throwable, E> {
    private final Class<E> type;

    private ExceptionFunction(Class<E> type) {
      this.type = requireNonNull(type);
    }

    @Override public E apply(Throwable throwable) {
      return newInstance(getConstructor(type, Throwable.class), throwable);
    }

    private static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... argTypes) {
      try {
        return type.getConstructor(argTypes);
      } catch (NoSuchMethodException e) {
        throw com.google.common.base.Throwables.propagate(e);
      }
    }

    private static <T> T newInstance(Constructor<T> constructor, Object... args) {
      try {
        return constructor.newInstance(args);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw com.google.common.base.Throwables.propagate(e);
      }
    }
  }
}
