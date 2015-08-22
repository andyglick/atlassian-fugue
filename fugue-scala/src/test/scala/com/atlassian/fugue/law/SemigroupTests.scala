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
import com.atlassian.fugue.law.SemigroupLaws.semigroupLaws
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

object SemigroupTests {

  def apply[A: Arbitrary](semigroup: Semigroup[A]) = new Properties("Semigroup") {

    val laws = semigroupLaws(semigroup)

    property("append is associative") = forAll((x: A, y: A, z: A) => laws.semigroupAssociative(x, y, z))

    val asBinaryOp = semigroup.asInstanceOf[BinaryOperator[A]]

    property("apply is same as append") = forAll((a1: A, a2: A) => isEq(asBinaryOp.apply(a1, a2), semigroup.append(a1, a2)))
  }

}
