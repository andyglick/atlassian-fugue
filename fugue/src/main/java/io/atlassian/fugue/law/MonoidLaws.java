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

import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Monoid;

import java.util.function.BiFunction;

/**
 * Laws for a monoid
 *
 */
public final class MonoidLaws<A> {

  private final Monoid<A> monoid;

  /**
   * Build a law instance to check monoid properties
   *
   * @param monoid a {@link io.atlassian.fugue.Monoid} to check matches the
   * desired behaviors of
   */
  public MonoidLaws(final Monoid<A> monoid) {
    this.monoid = monoid;
  }

  /**
   * A monoid must not care about the order elements are combined. If you
   * combine x with y and then with z the result should be the same as combining
   * y with z and then with x.
   *
   * @param x an A object
   * @param y an A object
   * @param z an A object
   * @return a {@link io.atlassian.fugue.law.IsEq} instance where
   * append(append(x,y),z) is equal to append(x, append(y,z))
   */
  public IsEq<A> semigroupAssociative(final A x, final A y, final A z) {
    return IsEq.isEq(monoid.append(monoid.append(x, y), z), monoid.append(x, monoid.append(y, z)));
  }

  /**
   * If the zero of your monoid is combine with an element of the type your
   * monoid works with the result should be that element.
   *
   * @param x an element of your monoid type
   * @return a {@link io.atlassian.fugue.law.IsEq} where x is equal to
   * append(zero(), x)
   */
  public IsEq<A> monoidLeftIdentity(final A x) {
    return IsEq.isEq(x, monoid.append(monoid.zero(), x));
  }

  /**
   * If an element of the type your monoid works with is combined with the zero
   * of your monoid result should be that element.
   *
   * @param x an element of your monoid type
   * @return a {@link io.atlassian.fugue.law.IsEq} where x is equal to append(x,
   * zero())
   */
  public IsEq<A> monoidRightIdentity(final A x) {
    return IsEq.isEq(x, monoid.append(x, monoid.zero()));
  }

  /**
   * The sum function of your monoid must be equal to
   * {@link Functions#fold(BiFunction, Object, Iterable)} using append as the
   * folding function and zero() as the initial value
   *
   * @param as a {@link java.lang.Iterable}
   * @return a {@link io.atlassian.fugue.law.IsEq} where sum(as) is equal to
   * fold(append, zero, as)
   */
  public IsEq<A> sumEqualFold(final Iterable<A> as) {
    return IsEq.isEq(monoid.sum(as), Functions.fold(monoid::append, monoid.zero(), as));
  }

  /**
   * The multiply function of your monoid must be equal to the sum function
   * called with an iterable containing {@code n} copies of the input type.
   *
   * @param n a int
   * @param a an A
   * @return a {@link io.atlassian.fugue.law.IsEq} where multiply(n,a) is equal
   * to sum(take(n, cycle(a))
   */
  public IsEq<A> multiplyEqualRepeatedAppend(final int n, final A a) {
    return IsEq.isEq(monoid.multiply(n, a), monoid.sum(Iterables.take(n, Iterables.cycle(a))));
  }

}
