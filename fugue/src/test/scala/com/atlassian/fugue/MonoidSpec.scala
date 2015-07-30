package com.atlassian.fugue

import com.atlassian.fugue.Monoid._
import com.atlassian.fugue.Semigroup.semigroup
import com.atlassian.fugue.law.discipline.MonoidTests


class MonoidSpec extends TestSuite {

  val intMonoid = monoid(semigroup((i1: Int, i2: Int) => i1 + i2), 0)

  val mulMonoid = monoid(semigroup((i1: Int, i2: Int) => i1 * i2), 1)

  checkAll("apply Int Monoid", MonoidTests(intMonoid))

  checkAll("apply Mul Monoid", MonoidTests(mulMonoid))

  test("sum") {
    intMonoid.sum(1, 2) shouldEqual 3
  }

  test("apply is same as sum") {
    intMonoid.sum(1, 2) shouldEqual 3
  }

  test("currying") {
    intMonoid.sum(1).apply(2) shouldEqual 3
  }

  checkAll("monoid composition", MonoidTests(intMonoid.composeMonoid(mulMonoid)))

}
