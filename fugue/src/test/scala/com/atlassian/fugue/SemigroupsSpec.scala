package com.atlassian.fugue

import java.math.{BigDecimal, BigInteger}
import java.util.Comparator

import com.atlassian.fugue.Semigroups._
import com.atlassian.fugue.law.SemigroupTests


class SemigroupsSpec extends TestSuite {

  test("intMaximum") {
    intMaximumSemigroup.append(1, 2) shouldEqual 2
    maxSemigroup(Comparator.naturalOrder[Integer]()).append(1, 2) shouldEqual 2
    maxSemigroup[Integer]().append(1, 2) shouldEqual 2
    check(SemigroupTests(intMaximumSemigroup))
  }
  test("intMaximum via comparator") { check(SemigroupTests(maxSemigroup(Comparator.naturalOrder[Integer]()))) }
  test("intMaximum via comparable") { check(SemigroupTests(maxSemigroup[Integer]())) }

  test("intMinimum") {
    intMinimumSemigroup.append(1, 2) shouldEqual 1
    minSemigroup(Comparator.naturalOrder[Integer]()).append(1, 2) shouldEqual 1
    minSemigroup[Integer]().append(1, 2) shouldEqual 1
    check(SemigroupTests(intMinimumSemigroup))
  }
  test("intMinimum via comparator") { check(SemigroupTests(minSemigroup(Comparator.naturalOrder[Integer]())))}
  test("intMinimum via comparable") { check(SemigroupTests(minSemigroup[Integer]()))}


  test("longMaximum") {
    longMaximumSemigroup.append(1L, 2L) shouldEqual 2L
    check(SemigroupTests(longMaximumSemigroup))
  }

  test("longMinimum") {
    longMinimumSemigroup.append(1L, 2L) shouldEqual 1L
    check(SemigroupTests(longMinimumSemigroup))
  }

  test("bigintMaximum") {
    bigintMaximumSemigroup.append(BigInteger.valueOf(1), BigInteger.valueOf(2)) shouldEqual BigInteger.valueOf(2)
    check(SemigroupTests(bigintMaximumSemigroup))
  }

  test("bigintMinimum") {
    bigintMinimumSemigroup.append(BigInteger.valueOf(1), BigInteger.valueOf(2)) shouldEqual BigInteger.valueOf(1)
    check(SemigroupTests(bigintMinimumSemigroup))
  }

  test("bigDecimalMaximum") {
    bigDecimalMaximumSemigroup.append(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0)) shouldEqual BigDecimal.valueOf(2.0)
    check(SemigroupTests(bigDecimalMaximumSemigroup))
  }

  test("bigDecimalMinimum") {
    bigDecimalMinimumSemigroup.append(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0)) shouldEqual BigDecimal.valueOf(1.0)
    check( SemigroupTests(bigDecimalMinimumSemigroup))
  }
}
