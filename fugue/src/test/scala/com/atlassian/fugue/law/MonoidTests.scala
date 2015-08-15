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

package com.atlassian.fugue.law

import com.atlassian.fugue.Monoid
import com.atlassian.fugue.law.MonoidLaws.monoidLaws
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

object MonoidTests {

  def apply[A: Arbitrary](monoid: Monoid[A]) = new Properties("monoid") {

    val laws = monoidLaws(monoid)

    property("semigroup") = SemigroupTests(monoid)

    property("left identity") = forAll((x: A) => laws.monoidLeftIdentity(x))

    property("right identity") = forAll((x: A) => laws.monoidRightIdentity(x))
  }

}
