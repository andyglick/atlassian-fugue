/*
   Copyright 2015 Atlassian

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.atlassian.fugue

import java.math.BigInteger
import java.util.Arrays.asList
import java.util.stream.{Collectors, StreamSupport}

import com.atlassian.fugue.Either.right
import com.atlassian.fugue.Monoids._
import com.atlassian.fugue.law.IsEq._
import com.atlassian.fugue.law.MonoidTests
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer


class MonoidsSpec extends TestSuite {

  test("intAddition") {
    intAdditionMonoid.append(1, 2) shouldEqual 3
    check(MonoidTests(intAdditionMonoid))
  }

  test("intMultiplication") {
    intMultiplicationMonoid.append(2, 3) shouldEqual 6
    check(MonoidTests(intMultiplicationMonoid))
  }

  test("doubleAddition") {
    doubleAdditionMonoid.append(2.0, 3.0) shouldEqual 5.0
    check(MonoidTests(doubleAdditionMonoid))
  }

  test("bigintAddition") {
    bigintAdditionMonoid.append(BigInteger.valueOf(2), BigInteger.valueOf(3)) shouldEqual BigInteger.valueOf(5)
    check(MonoidTests(bigintAdditionMonoid))
  }

  test("bigintMultiplication") {
    bigintMultiplicationMonoid.append(BigInteger.valueOf(2), BigInteger.valueOf(3)) shouldEqual BigInteger.valueOf(6)
    check(MonoidTests(bigintMultiplicationMonoid))
  }

  test("longAddition") {
    longAdditionMonoid.append(1L, 2L) shouldEqual 3L
    check(MonoidTests(longAdditionMonoid))
  }

  test("longMultiplication") {
    longMultiplicationMonoid.append(2L, 3L) shouldEqual 6L
    check(MonoidTests(longMultiplicationMonoid))
  }

  test("disjunction") {
    disjunctionMonoid.append(false, true) shouldEqual true
    disjunctionMonoid.append(true, true) shouldEqual true
    check(MonoidTests(disjunctionMonoid))
  }

  test("exclusiveDisjunction") {
    exclusiveDisjunctionMonoid.append(false, true) shouldEqual true
    exclusiveDisjunctionMonoid.append(true, true) shouldEqual false
    check(MonoidTests(exclusiveDisjunctionMonoid))
  }

  test("conjunction") {
    conjunctionMonoid.append(false, true) shouldEqual false
    conjunctionMonoid.append(true, true) shouldEqual true
    check(MonoidTests(conjunctionMonoid))
  }

  test("string") {
    stringMonoid.append("a", "b") shouldEqual "ab"
    check(MonoidTests(stringMonoid))
  }

  test("unit") {
    check(MonoidTests(unitMonoid))
  }

  test("list") {
    listMonoid[String]().append(asList("a"), asList("b")) shouldEqual asList("a", "b")
    check(MonoidTests(listMonoid[Integer]()))
  }

  test("iterable") {
    StreamSupport.stream(iterableMonoid[String]().append(asList("a"), asList("b")).spliterator(), false).collect(Collectors.toList[String]) shouldEqual asList("a", "b")
  }

  test("firstOption") {
    firstOptionMonoid[String]().append(Option.some("a"), Option.some("b")) shouldEqual Option.some("a")
    check(MonoidTests(firstOptionMonoid[Integer]()))
  }

  test("lastOption") {
    lastOptionMonoid[String]().append(Option.some("a"), Option.some("b")) shouldEqual Option.some("b")
    check(MonoidTests(lastOptionMonoid[Integer]()))
  }

  test("option") {
    optionMonoid(Semigroups.intAdditionSemigroup).append(Option.some(1), Option.some(2)) shouldEqual Option.some(3)
    check(MonoidTests(optionMonoid(Semigroups.intAdditionSemigroup)))
  }

  test("either") {
    val m = eitherMonoid(Semigroups.intAdditionSemigroup, stringMonoid)

    m.append(right("a"), right("b")) shouldEqual right("ab")
    m.append(Either.left(1), Either.left(2)) shouldEqual Either.left(3)
    check(MonoidTests(m))
  }

  test("Monoids derived methods") {
    check(derivedMethodsTests(stringMonoid))
  }

  test("Monoids composition") {
    check(MonoidTests(compose(stringMonoid, intAdditionMonoid)))
  }

  def derivedMethodsTests[A: Arbitrary](monoid: Monoid[A]) = new Properties("derived methods") {

    property("dual is also a monoid") = MonoidTests(Monoids.dual(monoid))

    property("concatInterspersed is consistent with concat") = forAll((a: A, aa: java.util.List[A]) => isEq(monoid.concat(Iterables.intersperse(aa, a)), Monoids.concatInterspersed(monoid, aa, a)))

    property("concatRepeated is consistent with concat") = sizedProp(n => forAll((a: A) => isEq(monoid.concat(asJavaIterable(ListBuffer.fill(n)(a))), Monoids.concatRepeated(monoid, n, a))))

  }

}
