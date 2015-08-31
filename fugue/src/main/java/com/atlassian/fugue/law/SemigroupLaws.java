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

package com.atlassian.fugue.law;

import com.atlassian.fugue.Functions;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Semigroup;

import static com.atlassian.fugue.law.IsEq.isEq;

/**
 * Laws for a semigroup
 */
public final class SemigroupLaws<A> {

  private final Semigroup<A> semigroup;

  public SemigroupLaws(Semigroup<A> semigroup) {
    this.semigroup = semigroup;
  }

  public IsEq<A> semigroupAssociative(A x, A y, A z) {
    return isEq(semigroup.append(semigroup.append(x, y), z), semigroup.append(x, semigroup.append(y, z)));
  }

  public IsEq<A> sumNelEqualFold(A head, Iterable<A> tail) {
    return isEq(semigroup.sumNel(head, tail), Functions.fold(semigroup::append, head, tail));
  }

  public IsEq<A> multiply1pEqualRepeatedAppend(int n, A a) {
    return isEq(semigroup.multiply1p(n, a), semigroup.sumNel(a, Iterables.take(n, Iterables.cycle(a))));
  }

}
