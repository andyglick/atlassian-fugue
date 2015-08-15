package com.atlassian.fugue.law

import com.atlassian.fugue.law.IsEq.isEq
import com.atlassian.fugue.{Iterables, Monoid}
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object MonoidDerivedFunctionsTests {

  def apply[A: Arbitrary](monoid: Monoid[A]) = new Properties("Monoid derived functions") {

    property("semigroup derived function") = SemigroupDerivedFunctionsTests(monoid)

    property("flipped is also a monoid") = MonoidTests(monoid.flipped())

    property("join is equivalent to fold") = forAll((aa: List[A]) => isEq(aa.fold(monoid.empty())((a1, a2) => monoid.append(a1, a2)), monoid.join(aa)))

    property("join is equivalent to  join") = forAll((aa: java.util.List[A]) => isEq(monoid.join(aa), monoid.join(aa.stream())))

    property("joinInterspersed is consistent with join") = forAll((a: A, aa: java.util.List[A]) => isEq(monoid.join(Iterables.intersperse(aa, a)), monoid.joinInterspersed(aa, a)))

    property("joinInterspersedStream is equivalent to joinInterspersed") = forAll((a: A, aa: java.util.List[A]) => isEq(monoid.joinInterspersed(aa, a), monoid.joinInterspersedStream(aa.stream(), a)))

    property("joinRepeated is consistent with join") = sizedProp(n => forAll((a: A) => isEq(monoid.join(asJavaIterable(ListBuffer.fill(n)(a))), monoid.joinRepeated(n, a))))

  }

}
