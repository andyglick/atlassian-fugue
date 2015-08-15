package com.atlassian.fugue.law

import com.atlassian.fugue.Semigroup
import com.atlassian.fugue.law.IsEq.isEq
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

object SemigroupDerivedFunctionsTests {

  def apply[A: Arbitrary](semigroup: Semigroup[A]) = new Properties("Semigroup derived functions") {

    property("apply is same as append") = forAll((a1: A, a2: A) => isEq(semigroup.apply(a1, a2), semigroup.append(a1, a2)))

    property("flipped is also a semigroup") = SemigroupTests(semigroup.flipped())

  }

}
