package com.atlassian.fugue.law.discipline

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import com.atlassian.fugue.{Iterables, Monoid}
import com.atlassian.fugue.law.MonoidLaws.monoidLaws
import com.atlassian.fugue.law.IsEq.isEq
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

object MonoidTests extends Laws {

  def apply[A: Arbitrary](monoid: Monoid[A]): RuleSet = {
    val laws = monoidLaws(monoid)
    new SimpleRuleSet("Monoid",
      "sum is associative" -> forAll( (x: A, y: A, z: A) => laws.semigroupAssociative(x, y, z)),
      "left identity"      -> forAll( (x: A) => laws.monoidLeftIdentity(x)),
      "right identity"     -> forAll( (x: A) => laws.monoidRightIdentity(x))
    )
  }

}
