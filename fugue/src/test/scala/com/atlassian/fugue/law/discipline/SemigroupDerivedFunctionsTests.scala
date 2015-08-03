package com.atlassian.fugue.law.discipline

import com.atlassian.fugue.law.IsEq.isEq
import com.atlassian.fugue.law.MonoidLaws.monoidLaws
import com.atlassian.fugue.{Semigroup, Iterables, Monoid}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object SemigroupDerivedFunctionsTests extends Laws {

  def apply[A: Arbitrary](semigroup: Semigroup[A]): RuleSet = {
    new SimpleRuleSet("Semigroup derived functions",

      "apply is same as sum" -> forAll( (a1: A, a2: A) => isEq(semigroup.apply(a1, a2), semigroup.sum(a1, a2))),
      "currying" -> forAll( (a1: A, a2: A) => isEq( semigroup.sum(a1, a2), semigroup.add(a1).apply(a2) ))
    )
  }

}
