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

package io.atlassian.fugue.law

import io.atlassian.fugue.Monoid
import org.scalacheck.Prop._
import org.scalacheck.{ Arbitrary, Properties }

import scala.collection.JavaConversions._

object MonoidTests {

  def apply[A: Arbitrary](monoid: Monoid[A]) = new Properties("monoid") {

    val laws = new MonoidLaws(monoid)

    property("append is associative") = forAll((x: A, y: A, z: A) => laws.semigroupAssociative(x, y, z))

    property("left identity") = forAll((x: A) => laws.monoidLeftIdentity(x))

    property("right identity") = forAll((x: A) => laws.monoidRightIdentity(x))

    property("sum is equivalent to fold") = forAll((aa: List[A]) => laws.sumEqualFold(aa))

    property("multiply is consistent with sum") = sizedProp(n => forAll((a: A) => laws.multiplyEqualRepeatedAppend(n, a)))

  }

}
