package com.atlassian.fugue

import java.math.BigInteger
import java.util.Arrays.asList

import com.atlassian.fugue.Monoids._
import com.atlassian.fugue.law.{MonoidDerivedFunctionsTests, MonoidTests}


class MonoidsSpec extends TestSuite {

  test("intAddition") {
    intAdditionMonoid.sum(1, 2) shouldEqual 3
    check(MonoidTests(intAdditionMonoid))
  }

  test("intMultiplication") {
    intMultiplicationMonoid.sum(2, 3) shouldEqual 6
    check(MonoidTests(intMultiplicationMonoid))
  }

  test("doubleAddition") {
    doubleAdditionMonoid.sum(2.0, 3.0) shouldEqual 5.0
    check(MonoidTests(doubleAdditionMonoid))
  }

  test("bigintAddition") {
    bigintAdditionMonoid.sum(BigInteger.valueOf(2), BigInteger.valueOf(3)) shouldEqual BigInteger.valueOf(5)
    check(MonoidTests(bigintAdditionMonoid))
  }

  test("bigintMultiplication") {
    bigintMultiplicationMonoid.sum(BigInteger.valueOf(2), BigInteger.valueOf(3)) shouldEqual BigInteger.valueOf(6)
    check(MonoidTests(bigintMultiplicationMonoid))
  }

  test("longAddition") {
    longAdditionMonoid.sum(1L, 2L) shouldEqual 3L
    check(MonoidTests(longAdditionMonoid))
  }

  test("longMultiplication") {
    longMultiplicationMonoid.sum(2L, 3L) shouldEqual 6L
    check(MonoidTests(longMultiplicationMonoid))
  }

  test("disjunction") {
    disjunctionMonoid.sum(false, true) shouldEqual true
    disjunctionMonoid.sum(true, true) shouldEqual true
    check(MonoidTests(disjunctionMonoid))
  }

  test("exclusiveDisjunction") {
    exclusiveDisjunctionMonoid.sum(false, true) shouldEqual true
    exclusiveDisjunctionMonoid.sum(true, true) shouldEqual false
    check(MonoidTests(exclusiveDisjunctionMonoid))
  }

  test("conjunction") {
    conjunctionMonoid.sum(false, true) shouldEqual false
    conjunctionMonoid.sum(true, true) shouldEqual true
    check(MonoidTests(conjunctionMonoid))
  }

  test("string") {
    stringMonoid.sum("a", "b") shouldEqual "ab"
    check(MonoidTests(stringMonoid))
    check(MonoidDerivedFunctionsTests(stringMonoid))
  }

  test("unit") {
    check(MonoidTests(unitMonoid))
  }

  test("list") {
    listMonoid[String]().sum(asList("a"), asList("b")) shouldEqual asList("a", "b")
    check(MonoidTests(listMonoid[Integer]()))
    check(MonoidDerivedFunctionsTests(listMonoid[Integer]()))
  }

  test("option") {
    optionMonoid[String]().sum(Option.some("a"), Option.some("b")) shouldEqual Option.some("a")
    check(MonoidTests(optionMonoid[Integer]()))
  }

  test("lastOption") {
    lastOptionMonoid[String]().sum(Option.some("a"), Option.some("b")) shouldEqual Option.some("b")
    check(MonoidTests(lastOptionMonoid[Integer]()))
  }


}
