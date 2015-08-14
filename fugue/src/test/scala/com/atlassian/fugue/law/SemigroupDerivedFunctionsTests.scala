package com.atlassian.fugue.law

import com.atlassian.fugue.Semigroup
import com.atlassian.fugue.law.IsEq.isEq
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

object SemigroupDerivedFunctionsTests {

  def apply[A: Arbitrary](semigroup: Semigroup[A]) = new Properties("Semigroup derived functions") {


    property("apply is same as sum") = forAll((a1: A, a2: A) => isEq(semigroup.apply(a1, a2), semigroup.sum(a1, a2)))

    property("currying") = forAll((a1: A, a2: A) => isEq(semigroup.sum(a1, a2), semigroup.add(a1).apply(a2)))

  }

}
