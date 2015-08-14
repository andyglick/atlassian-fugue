package com.atlassian.fugue.law

import com.atlassian.fugue.law.IsEq.isEq
import com.atlassian.fugue.{Iterables, Monoid}
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object MonoidDerivedFunctionsTests {

  def apply[A: Arbitrary](monoid: Monoid[A]) = new Properties("Monoid derived functions") {

    SemigroupDerivedFunctionsTests(monoid)

    property("sumIterable is equivalent to fold") = forAll((aa: List[A]) => isEq(aa.fold(monoid.zero())((a1, a2) => monoid.sum(a1, a2)), monoid.sumIterable(aa)))

    property("sumStream is equivalent to  sumIterable") = forAll((aa: java.util.List[A]) => isEq(monoid.sumIterable(aa), monoid.sumStream(aa.stream())))

    property("join is consistent with sumIterable") = forAll((a: A, aa: java.util.List[A]) => isEq(monoid.sumIterable(Iterables.intersperse(aa, a)), monoid.join(aa, a)))

    property("joinStream is equivalent to join") = forAll((a: A, aa: java.util.List[A]) => isEq(monoid.join(aa, a), monoid.joinStream(aa.stream(), a)))

    property("multiply is consistent with sumIterable") = sizedProp(n => forAll((a: A) => isEq(monoid.sumIterable(asJavaIterable(ListBuffer.fill(n)(a))), monoid.multiply(n, a))))

  }

}
