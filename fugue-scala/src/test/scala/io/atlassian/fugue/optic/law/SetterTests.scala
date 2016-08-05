package io.atlassian.fugue.optic.law

import io.atlassian.fugue.optic.PSetter
import org.scalacheck.Prop._
import org.scalacheck.{ Properties, Arbitrary }

object SetterTests {

  def apply[S: Arbitrary, A: Arbitrary](setter: PSetter[S, S, A, A]) = new Properties("setter") {

    val laws: SetterLaws[S, A] = new SetterLaws(setter)

    property("set idempotent") = forAll((s: S, a: A) => laws.setIdempotent(s, a))

    property("modify id = id") = forAll((s: S) => laws.modifyIdentity(s))
  }

}
