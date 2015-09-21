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
import io.atlassian.fugue.Monoid;

import static io.atlassian.fugue.law.IsEq.isEq;

/**
 * Laws for a monoid
 */
public final class MonoidLaws<A> {

  private final Monoid<A> monoid;

  public MonoidLaws(Monoid<A> monoid) {
    this.monoid = monoid;
  }

  public IsEq<A> semigroupAssociative(A x, A y, A z) {
    return IsEq.isEq(monoid.append(monoid.append(x, y), z), monoid.append(x, monoid.append(y, z)));
  }

  public IsEq<A> monoidLeftIdentity(A x) {
    return IsEq.isEq(x, monoid.append(monoid.zero(), x));
  }

  public IsEq<A> monoidRightIdentity(A x) {
    return IsEq.isEq(x, monoid.append(x, monoid.zero()));
  }

  public IsEq<A> sumEqualFold(Iterable<A> as) {
    return IsEq.isEq(monoid.sum(as), Functions.fold(monoid::append, monoid.zero(), as));
  }

  public IsEq<A> multiplyEqualRepeatedAppend(int n, A a) {
    return IsEq.isEq(monoid.multiply(n, a), monoid.sum(Iterables.take(n, Iterables.cycle(a))));
  }

}
