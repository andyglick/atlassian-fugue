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

/**
 * Laws for a semigroup
 */
public final class SemigroupLaws<A> {

  private final Semigroup<A> semigroup;

  public SemigroupLaws(Semigroup<A> semigroup) {
    this.semigroup = semigroup;
  }

  public IsEq<A> semigroupAssociative(A x, A y, A z) {
    return IsEq.isEq(semigroup.append(semigroup.append(x, y), z), semigroup.append(x, semigroup.append(y, z)));
  }

  public IsEq<A> sumNonEmptyEqualFold(A head, Iterable<A> tail) {
    return IsEq.isEq(semigroup.sumNonEmpty(head, tail), Functions.fold(semigroup::append, head, tail));
  }

  public IsEq<A> multiply1pEqualRepeatedAppend(int n, A a) {
    return IsEq.isEq(semigroup.multiply1p(n, a), semigroup.sumNonEmpty(a, Iterables.take(n, Iterables.cycle(a))));
  }

}
