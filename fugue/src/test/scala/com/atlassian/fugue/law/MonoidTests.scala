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
