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
