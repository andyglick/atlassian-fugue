package com.atlassian.fugue

import java.math.{BigDecimal, BigInteger}
import java.util.Comparator

import com.atlassian.fugue.Semigroups._
import com.atlassian.fugue.law.discipline.SemigroupTests


class SemigroupsSpec extends TestSuite {

  test("intMaximum") {
    intMaximumSemigroup.sum(1, 2) shouldEqual 2
    maxSemigroup(Comparator.naturalOrder[Integer]()).sum(1, 2) shouldEqual 2
    maxSemigroup[Integer]().sum(1, 2) shouldEqual 2
  }
  checkAll("intMaximum law", SemigroupTests(intMaximumSemigroup))
  checkAll("intMaximum via comparator law", SemigroupTests(maxSemigroup(Comparator.naturalOrder[Integer]())))
  checkAll("intMaximum via comparable law", SemigroupTests(maxSemigroup[Integer]()))

  test("intMinimum") {
    intMinimumSemigroup.sum(1, 2) shouldEqual 1
    minSemigroup(Comparator.naturalOrder[Integer]()).sum(1, 2) shouldEqual 1
    minSemigroup[Integer]().sum(1, 2) shouldEqual 1
  }
  checkAll("intMinimum law", SemigroupTests(intMinimumSemigroup))
  checkAll("intMinimum via comparator law", SemigroupTests(minSemigroup(Comparator.naturalOrder[Integer]())))
  checkAll("intMinimum via comparable law", SemigroupTests(minSemigroup[Integer]()))


  test("longMaximum") {
    longMaximumSemigroup.sum(1L, 2L) shouldEqual 2L
  }
  checkAll("longMaximum law", SemigroupTests(longMaximumSemigroup))

  test("longMinimum") {
    longMinimumSemigroup.sum(1L, 2L) shouldEqual 1L
  }
  checkAll("longMinimum law", SemigroupTests(longMinimumSemigroup))

  test("bigintMaximum") {
    bigintMaximumSemigroup.sum(BigInteger.valueOf(1), BigInteger.valueOf(2)) shouldEqual BigInteger.valueOf(2)
  }
  checkAll("bigintMaximum law", SemigroupTests(bigintMaximumSemigroup))

  test("bigintMinimum") {
    bigintMinimumSemigroup.sum(BigInteger.valueOf(1), BigInteger.valueOf(2)) shouldEqual BigInteger.valueOf(1)
  }
  checkAll("bigintMinimum law", SemigroupTests(bigintMinimumSemigroup))

  test("bigDecimalMaximum") {
    bigDecimalMaximumSemigroup.sum(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0)) shouldEqual BigDecimal.valueOf(2.0)
  }
  checkAll("bigDecimalMaximum law", SemigroupTests(bigDecimalMaximumSemigroup))

  test("bigDecimalMinimum") {
    bigDecimalMinimumSemigroup.sum(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0)) shouldEqual BigDecimal.valueOf(1.0)
  }
  checkAll("bigDecimalMinimum law", SemigroupTests(bigDecimalMinimumSemigroup))
}
