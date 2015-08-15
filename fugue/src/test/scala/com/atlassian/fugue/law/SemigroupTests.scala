package com.atlassian.fugue.law

import com.atlassian.fugue.Semigroup
import com.atlassian.fugue.law.SemigroupLaws.semigroupLaws
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

object SemigroupTests {

  def apply[A: Arbitrary](semigroup: Semigroup[A]) = new Properties("Semigroup") {

    val laws = semigroupLaws(semigroup)

    property("append is associative") = forAll((x: A, y: A, z: A) => laws.semigroupAssociative(x, y, z))
  }

}
