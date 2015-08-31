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

import java.util.function.BinaryOperator

import com.atlassian.fugue.Semigroup
import com.atlassian.fugue.law.IsEq._
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object SemigroupTests {

  def apply[A: Arbitrary](semigroup: Semigroup[A]) = new Properties("Semigroup") {

    val laws = new SemigroupLaws(semigroup)

    property("append is associative") = forAll((x: A, y: A, z: A) => laws.semigroupAssociative(x, y, z))

    property("sumNel is equivalent to fold") = forAll((a: A, aa: List[A]) => laws.sumNelEqualFold(a, aa))

    property("multiply1p is consistent with sumNel") = sizedProp(n => forAll((a: A) => laws.multiply1pEqualRepeatedAppend(n, a)))

  }

}
