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

import com.atlassian.fugue.Semigroup;

import static com.atlassian.fugue.law.IsEq.isEq;

/**
 * Laws for a semigroup
 */
public interface SemigroupLaws<A> {

  Semigroup<A> semigroup();

  default IsEq<A> semigroupAssociative(A x, A y, A z) {
    return isEq(semigroup().append(semigroup().append(x, y), z), semigroup().append(x, semigroup().append(y, z)));
  }

  static <A> SemigroupLaws<A> semigroupLaws(Semigroup<A> semigroup) {
    return () -> semigroup;
  }

}
