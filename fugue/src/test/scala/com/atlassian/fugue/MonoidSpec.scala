package com.atlassian.fugue

import com.atlassian.fugue.Monoid._
import com.atlassian.fugue.Semigroup.semigroup
import com.atlassian.fugue.law.discipline.{MonoidDerivedFunctionsTests, MonoidTests}


class MonoidSpec extends TestSuite {

  val intMonoid = monoid(semigroup((i1: Int, i2: Int) => i1 + i2), 0)

  checkAll("Monoid", MonoidTests(intMonoid))

  checkAll("Monoid derived functions", MonoidDerivedFunctionsTests(intMonoid))

  val mulMonoid = monoid(semigroup((i1: Int, i2: Int) => i1 * i2), 1)

  checkAll("Monoid composition", MonoidTests(intMonoid.composeMonoid(mulMonoid)))

}
