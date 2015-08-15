package com.atlassian.fugue

import java.math.BigInteger
import java.util.Arrays.asList
import java.util.stream.{Collectors, StreamSupport}

import com.atlassian.fugue.Either.right
import com.atlassian.fugue.Monoids._
import com.atlassian.fugue.law.{MonoidDerivedFunctionsTests, MonoidTests}


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
    check(MonoidDerivedFunctionsTests(stringMonoid))
  }

  test("unit") {
    check(MonoidTests(unitMonoid))
  }

  test("list") {
    listMonoid[String]().append(asList("a"), asList("b")) shouldEqual asList("a", "b")
    check(MonoidTests(listMonoid[Integer]()))
    check(MonoidDerivedFunctionsTests(listMonoid[Integer]()))
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

}
