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

package com.atlassian.fugue

import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.scalacheck.util.Pretty
import org.scalacheck.{Arbitrary, Prop}

import scala.collection.JavaConversions._

package object law {

  implicit def isEqToProp[A](isEq: IsEq[A]): Prop =
    if (if (isEq.lhs().isInstanceOf[Comparable[_]]) isEq.lhs().asInstanceOf[Comparable[A]].compareTo(isEq.rhs()) == 0
    else isEq.lhs().equals(isEq.rhs())) proved
    else falsified :| {
      val exp = Pretty.pretty[A](isEq.lhs(), Pretty.Params(0))
      val act = Pretty.pretty[A](isEq.rhs(), Pretty.Params(0))
      "Expected " + exp + " but got " + act
    }

  implicit def javaListArbitrary[A: Arbitrary]: Arbitrary[java.util.List[A]] =
    Arbitrary(arbitrary[scala.List[A]] map seqAsJavaList)

}
