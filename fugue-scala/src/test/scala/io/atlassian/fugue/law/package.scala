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

package io.atlassian.fugue

import org.scalacheck.Prop
import org.scalacheck.Prop._
import org.scalacheck.util.Pretty

package object law {

  implicit def isEqToProp[A](isEq: IsEq[A]): Prop =
    if (isEq.lhs().isInstanceOf[Comparable[_]] && isEq.lhs().asInstanceOf[Comparable[A]].compareTo(isEq.rhs()) == 0) proved
    else if (isEq.lhs().equals(isEq.rhs())) proved
    else falsified :| {
      val exp = Pretty.pretty[A](isEq.lhs(), Pretty.Params(0))
      val act = Pretty.pretty[A](isEq.rhs(), Pretty.Params(0))
      "Expected " + exp + " but got " + act
    }
}
