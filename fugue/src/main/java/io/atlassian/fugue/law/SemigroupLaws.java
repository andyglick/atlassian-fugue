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
import io.atlassian.fugue.Semigroup;

import java.util.function.BiFunction;

/**
 * Laws for a semigroup
 */
public final class SemigroupLaws<A> {

  private final Semigroup<A> semigroup;

  /**
   * Build a law instance to check semigroup properties
   *
   * @param semigroup a {@link io.atlassian.fugue.Semigroup} to check matches
   * the desired behaviors of
   */
  public SemigroupLaws(final Semigroup<A> semigroup) {
    this.semigroup = semigroup;
  }

  /**
   * A semigroup must not care about the order elements are combined. If you
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
    return IsEq.isEq(semigroup.append(semigroup.append(x, y), z), semigroup.append(x, semigroup.append(y, z)));
  }

  /**
   * The {@link Semigroup#sumNonEmpty(Object, Iterable)} function of your
   * semigroup must be equal to a
   * {@link Functions#fold(BiFunction, Object, Iterable)} where append is used
   * as the combining function, head is used as the initial value and tail is
   * the iterable to check
   *
   * @param head an A
   * @param tail a {@link java.lang.Iterable} of A's
   * @return a {@link io.atlassian.fugue.law.IsEq} where sumeNonEmpty(head,
   * tail) is equal to fold(append, head, tail)
   */
  public IsEq<A> sumNonEmptyEqualFold(final A head, final Iterable<A> tail) {
    return IsEq.isEq(semigroup.sumNonEmpty(head, tail), Functions.fold(semigroup::append, head, tail));
  }

  /**
   * The {@link Semigroup#multiply1p(int, Object)} function of your semigroup
   * must be equal to {@link Semigroup#sumNonEmpty(Object, Iterable)} applied to
   * the input and an iterable containing {@code n - 1} copies of that input
   *
   * @param n a int representing the number of copies of the input to combine
   * @param a an A
   * @return a {@link io.atlassian.fugue.law.IsEq} where multiply(n,a) is equal
   * to sumNonEmpty(a, take(n-1, cycle(a))
   */
  public IsEq<A> multiply1pEqualRepeatedAppend(final int n, final A a) {
    return IsEq.isEq(semigroup.multiply1p(n, a), semigroup.sumNonEmpty(a, Iterables.take(n, Iterables.cycle(a))));
  }

}
