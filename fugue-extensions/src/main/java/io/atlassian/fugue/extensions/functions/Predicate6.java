package io.atlassian.fugue.extensions.functions;

/**
 * Represents a predicate (boolean-valued function) of five arguments.
 * <p>
 * This is afunctional interface whose functional method is
 * {@link #test(Object, Object, Object, Object, Object, Object)}.
 *
 * @param <A> the type of the first argument to the predicate
 * @param <B> the type of the second argument to the predicate
 * @param <C> the type of the third argument to the predicate
 * @param <D> the type of the fourth argument to the predicate
 * @param <E> the type of the fifth argument to the predicate
 * @param <F> the type of the sixth argument to the predicate
 * @see java.util.function.Predicate
 * @see java.util.function.BiPredicate
 * @since 4.7.0
 */
@FunctionalInterface public interface Predicate6<A, B, C, D, E, F> {

  /**
   * Evaluates this predicate on the given arguments.
   *
   * @param a the first input argument
   * @param b the second input argument
   * @param c the third input argument
   * @param d the fourth input argument
   * @param e the fifth input argument
   * @param f the sixth input argument
   * @return {@code true} if the input arguments match the predicate, otherwise
   * {@code false}
   */
  boolean test(A a, B b, C c, D d, E e, F f);

}
