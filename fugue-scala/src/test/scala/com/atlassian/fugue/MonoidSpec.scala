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

import com.atlassian.fugue.Monoid._
import com.atlassian.fugue.Semigroup.semigroup
import com.atlassian.fugue.law.IsEq._
import com.atlassian.fugue.law.MonoidTests
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

import com.atlassian.fugue.converters.ScalaConverters._


class MonoidSpec extends TestSuite {
  val intMonoid = monoid(semigroup(((i1: Int) => (i2: Int) => i1 + i2).toJava), 0.toJava)

  test("Monoid laws") {
    check(MonoidTests(intMonoid))
  }

  val stringMonoid = monoid(semigroup(((s1: String) => (s2: String) => s1 + s2).toJava), "")

  test("Monoids derived methods") {
    check(derivedMethodsTests(stringMonoid))
  }

  test("Monoids composition") {
    check(MonoidTests(compose(stringMonoid, intMonoid)))
  }

  def derivedMethodsTests[A: Arbitrary](monoid: Monoid[A]) = new Properties("derived methods") {

    property("dual is also a monoid") = MonoidTests(Monoid.dual(intMonoid))

    property("intersperse is consistent with sum") = forAll((a: A, aa: java.util.List[A]) => isEq(monoid.sum(Iterables.intersperse(aa, a)), Monoid.intersperse(monoid, aa, a)))

  }

}
