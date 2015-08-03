package com.atlassian.fugue.law.discipline

import com.atlassian.fugue.law.SemigroupLaws
import com.atlassian.fugue.law.SemigroupLaws.semigroupLaws
import com.atlassian.fugue.{Semigroup, Monoid}
import com.atlassian.fugue.law.MonoidLaws.monoidLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

object SemigroupTests extends Laws {

  def apply[A: Arbitrary](semigroup: Semigroup[A]): RuleSet = {
    val laws = semigroupLaws(semigroup)
    new SimpleRuleSet("Semigroup",
      "sum is associative" -> forAll( (x: A, y: A, z: A) => laws.semigroupAssociative(x, y, z))
    )
  }

}
