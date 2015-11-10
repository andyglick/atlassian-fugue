/*
   Copyright 2015 Atlassian

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
package io.atlassian.fugue.law;

import java.util.function.BiFunction;

/**
 * Represents two values of the same type that are expected to be equal.
 *
 */
public final class IsEq<A> {

  private final A lhs;

  private final A rhs;

  /**
   * Build an equality instance
   *
   * @param lhs first value to compare
   * @param rhs second value to compare
   */
  public IsEq(final A lhs, final A rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  /**
   * Function to use for comparison of two equal elements
   *
   * @param cases a {@link java.util.function.BiFunction} taking two values of this equality class
   * @param <R> result type
   * @return a R object.
   */
  public <R> R match(final BiFunction<A, A, R> cases) {
    return cases.apply(lhs, rhs);
  }

  /**
   * Access the left element
   *
   * @return left hand side,
   */
  public A lhs() {
    return lhs;
  }

  /**
   * Access the right element
   *
   * @return right hand side,
   */
  public A rhs() {
    return rhs;
  }

  /**
   * Two equal elements
   *
   * @param lhs an A
   * @param rhs an A
   * @param <A> equality type
   * @return a {@link io.atlassian.fugue.law.IsEq} instance
   */
  public static <A> IsEq<A> isEq(final A lhs, final A rhs) {
    return new IsEq<>(lhs, rhs);
  }
}
