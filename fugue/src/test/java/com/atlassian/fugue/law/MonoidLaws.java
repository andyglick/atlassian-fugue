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

import com.atlassian.fugue.Monoid;
import com.atlassian.fugue.Semigroup;

import static com.atlassian.fugue.law.IsEq.isEq;

/**
 * Laws for a monoid
 */
public interface MonoidLaws<A> extends SemigroupLaws<A> {

  Monoid<A> monoid();

  @Override default Semigroup<A> semigroup() {
    return monoid();
  }

  default IsEq<A> monoidLeftIdentity(A x) {
    return isEq(x, monoid().append(monoid().empty(), x));
  }

  default IsEq<A> monoidRightIdentity(A x) {
    return isEq(x, monoid().append(x, monoid().empty()));
  }

  static <A> MonoidLaws<A> monoidLaws(Monoid<A> monoid) {
    return () -> monoid;
  }

}
