package com.atlassian.fugue;

/**
 * Represents a function that takes two parameters.
 * 
 * @param <F1> the type of the first argument accepted
 * @param <F2> the type of the result
 * @param <T> the type of the result of application
 */
public interface Function2<F1, F2, T> {
  /**
   * Returns the results of applying this function to the supplied arguments.
   * Like Guava's Function, this method is <em>generally expected</em>, but not
   * absolutely required, to have the following properties: * it's execution
   * does not cause any observable side effect * the computation is
   * <em>consistent with equals</em>; that is, Objects.equal(a, b) implies that
   * Objects.equal(function.apply(a), function.apply(b)).
   */
  T apply(F1 arg1, F2 arg2);
}
