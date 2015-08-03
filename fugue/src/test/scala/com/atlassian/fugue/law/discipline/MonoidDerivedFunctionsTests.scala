package com.atlassian.fugue.law.discipline

import com.atlassian.fugue.law.IsEq.isEq
import com.atlassian.fugue.law.MonoidLaws.monoidLaws
import com.atlassian.fugue.{Iterables, Monoid}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object MonoidDerivedFunctionsTests extends Laws {

  def apply[A: Arbitrary](monoid: Monoid[A]): RuleSet = {
    val laws = monoidLaws(monoid)
    new SimpleRuleSet("Monoid derived functions",

      "apply is same as sum" -> forAll( (a1: A, a2: A) => isEq(monoid.apply(a1, a2), monoid.sum(a1, a2))),
      "currying" -> forAll( (a1: A, a2: A) => isEq( monoid.sum(a1, a2), monoid.add(a1).apply(a2) )),
      "sumIterable is equivalent to fold"    -> forAll( (aa: List[A]) => isEq( aa.fold(monoid.zero())((a1, a2) => monoid.sum(a1, a2)), monoid.sumIterable(aa) )),
      "sumStream is equivalent to  sumIterable" -> forAll( (aa: java.util.List[A]) => isEq(monoid.sumIterable(aa), monoid.sumStream(aa.stream()))),
      "join is consistent with sumIterable"       -> forAll( (a : A, aa: java.util.List[A]) => isEq( monoid.sumIterable(Iterables.intersperse(aa, a)), monoid.join(aa, a) )),
      "joinStream is equivalent to join"       -> forAll( (a : A, aa: java.util.List[A]) => isEq(monoid.join(aa, a), monoid.joinStream(aa.stream(), a))),
      "multiply is consistent with sumIterable"    -> sizedProp( n => forAll( (a: A) => isEq( monoid.sumIterable(asJavaIterable(ListBuffer.fill(n)(a))), monoid.multiply(n, a) ) ) )
    )
  }

}
