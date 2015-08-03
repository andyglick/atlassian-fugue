package com.atlassian.fugue

import java.math.BigInteger
import java.util
import java.util.Arrays.asList

import com.atlassian.fugue.Monoids._
import com.atlassian.fugue.law.discipline.{MonoidDerivedFunctionsTests, MonoidTests}


class MonoidsSpec extends TestSuite {

  test("intAddition") {
    intAdditionMonoid.sum(1, 2) shouldEqual 3
  }
  checkAll("intAddition laws", MonoidTests(intAdditionMonoid))


  test("intMultiplication") {
    intMultiplicationMonoid.sum(2, 3) shouldEqual 6
  }
  checkAll("intMultiplication laws", MonoidTests(intMultiplicationMonoid))

  test("doubleAddition") {
    doubleAdditionMonoid.sum(2.0, 3.0) shouldEqual 5.0
  }
  checkAll("doubleAddition laws", MonoidTests(doubleAdditionMonoid))

  test("bigintAddition") {
    bigintAdditionMonoid.sum(BigInteger.valueOf(2), BigInteger.valueOf(3)) shouldEqual BigInteger.valueOf(5)
  }
  checkAll("bigintAddition laws", MonoidTests(bigintAdditionMonoid))

  test("bigintMultiplication") {
    bigintMultiplicationMonoid.sum(BigInteger.valueOf(2), BigInteger.valueOf(3)) shouldEqual BigInteger.valueOf(6)
  }
  checkAll("bigintMultiplication laws", MonoidTests(bigintMultiplicationMonoid))

  test("longAddition") {
    longAdditionMonoid.sum(1L, 2L) shouldEqual 3L
  }
  checkAll("longAddition laws", MonoidTests(longAdditionMonoid))

  test("longMultiplication") {
    longMultiplicationMonoid.sum(2L, 3L) shouldEqual 6L
  }
  checkAll("longMultiplication laws", MonoidTests(longMultiplicationMonoid))

  test("disjunction") {
    disjunctionMonoid.sum(false, true) shouldEqual true
    disjunctionMonoid.sum(true, true) shouldEqual true
  }
  checkAll("disjunction laws", MonoidTests(disjunctionMonoid))

  test("exclusiveDisjunction") {
    exclusiveDisjunctionMonoid.sum(false, true) shouldEqual true
    exclusiveDisjunctionMonoid.sum(true, true) shouldEqual false
  }
  checkAll("exclusiveDisjunction laws", MonoidTests(exclusiveDisjunctionMonoid))

  test("conjunction") {
    conjunctionMonoid.sum(false, true) shouldEqual false
    conjunctionMonoid.sum(true, true) shouldEqual true
  }
  checkAll("conjunction laws", MonoidTests(conjunctionMonoid))

  test("string") {
    stringMonoid.sum("a", "b") shouldEqual "ab"
  }
  checkAll("string laws", MonoidTests(stringMonoid))
  checkAll("string derived functions", MonoidDerivedFunctionsTests(stringMonoid))

  checkAll("unitMonoid", MonoidTests(unitMonoid))

  test("list") {
    listMonoid[String]().sum(asList("a"), asList("b")) shouldEqual asList("a", "b")
  }
  checkAll("list laws", MonoidTests(listMonoid[Integer]()))
  checkAll("list derived functions", MonoidDerivedFunctionsTests(listMonoid[Integer]()))

  test("option") {
    optionMonoid[String]().sum(Option.some("a"), Option.some("b")) shouldEqual Option.some("a")
  }
  checkAll("option laws", MonoidTests(optionMonoid[Integer]()))

  test("lastOption") {
    lastOptionMonoid[String]().sum(Option.some("a"), Option.some("b")) shouldEqual Option.some("b")
  }
  checkAll("lastOption laws", MonoidTests(lastOptionMonoid[Integer]()))

}
