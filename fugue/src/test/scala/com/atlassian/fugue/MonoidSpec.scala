package com.atlassian.fugue

import com.atlassian.fugue.Monoid._
import com.atlassian.fugue.Semigroup.semigroup
import com.atlassian.fugue.law.{MonoidDerivedFunctionsTests, MonoidTests}


class MonoidSpec extends TestSuite {

  val intMonoid = monoid(semigroup((i1: Int, i2: Int) => i1 + i2), 0)

  test("Monoid laws") {
    check(MonoidTests(intMonoid))
  }

  test("Monoid derived functions") {
    check(MonoidDerivedFunctionsTests(intMonoid))
  }

  val mulMonoid = monoid(semigroup((i1: Int, i2: Int) => i1 * i2), 1)

  test("Monoid composition") {
    check(MonoidTests(intMonoid.composeMonoid(mulMonoid)))
  }

}
