package io.atlassian.fugue.extensions.functions;

/**
 * Represents a function that accepts four arguments and produces a result.
 * <p>
 * This is a functional interface whose functional method is
 * {@link #apply(Object, Object, Object, Object)}.
 *
 * @param <A> the type of the first argument to the function
 * @param <B> the type of the second argument to the function
 * @param <C> the type of the third argument to the function
 * @param <D> the type of the fourth argument to the function
 * @param <Z> the type of the result of the function
 * @see java.util.function.Function
 * @see java.util.function.BiFunction
 * @since 4.7.0
 */
@FunctionalInterface public interface Function4<A, B, C, D, Z> {

  /**
   * Applies this function to the given arguments.
   *
   * @param a the first function argument
   * @param b the second function argument
   * @param c the third function argument
   * @param d the fourth function argument
   * @return the function result
   */
  Z apply(A a, B b, C c, D d);

}
