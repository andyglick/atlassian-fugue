package io.atlassian.fugue.optic.law

import io.atlassian.fugue.law.IsEq
import org.scalacheck.Prop
import org.scalacheck.Prop._
import org.scalacheck.util.Pretty

package object discipline {
  implicit def isEqToProp[A](isEq: IsEq[A]): Prop =
    if (isEq.lhs().equals(isEq.rhs())) proved else falsified :| {
      val exp = Pretty.pretty[A](isEq.lhs(), Pretty.Params(0))
      val act = Pretty.pretty[A](isEq.rhs(), Pretty.Params(0))
      "Expected " + exp + " but got " + act
    }
}
