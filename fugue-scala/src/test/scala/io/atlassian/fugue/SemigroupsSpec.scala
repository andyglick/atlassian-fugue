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

package io.atlassian.fugue

import java.math.{ BigDecimal, BigInteger }
import java.util.Comparator

import Semigroups._
import io.atlassian.fugue.law.SemigroupTests

class SemigroupsSpec extends TestSuite {

  test("intMaximum") {
    intMaximum.append(1, 2) shouldEqual 2
    max(Comparator.naturalOrder[Integer]()).append(1, 2) shouldEqual 2
    max[Integer]().append(1, 2) shouldEqual 2
    check(SemigroupTests(intMaximum))
  }
  test("intMaximum via comparator") {
    check(SemigroupTests(max(Comparator.naturalOrder[Integer]())))
  }
  test("intMaximum via comparable") {
    check(SemigroupTests(max[Integer]()))
  }

  test("intMinimum") {
    intMinimum.append(1, 2) shouldEqual 1
    min(Comparator.naturalOrder[Integer]()).append(1, 2) shouldEqual 1
    min[Integer]().append(1, 2) shouldEqual 1
    check(SemigroupTests(intMinimum))
  }
  test("intMinimum via comparator") {
    check(SemigroupTests(min(Comparator.naturalOrder[Integer]())))
  }
  test("intMinimum via comparable") {
    check(SemigroupTests(min[Integer]()))
  }

  test("longMaximum") {
    longMaximum.append(1L, 2L) shouldEqual 2L
    check(SemigroupTests(longMaximum))
  }

  test("longMinimum") {
    longMinimum.append(1L, 2L) shouldEqual 1L
    check(SemigroupTests(longMinimum))
  }

  test("bigintMaximum") {
    bigintMaximum.append(BigInteger.valueOf(1), BigInteger.valueOf(2)) shouldEqual BigInteger.valueOf(2)
    check(SemigroupTests(bigintMaximum))
  }

  test("bigintMinimum") {
    bigintMinimum.append(BigInteger.valueOf(1), BigInteger.valueOf(2)) shouldEqual BigInteger.valueOf(1)
    check(SemigroupTests(bigintMinimum))
  }

  test("bigDecimalMaximum") {
    bigDecimalMaximum.append(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0)) shouldEqual BigDecimal.valueOf(2.0)
    check(SemigroupTests(bigDecimalMaximum))
  }

  test("bigDecimalMinimum") {
    bigDecimalMinimum.append(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0)) shouldEqual BigDecimal.valueOf(1.0)
    check(SemigroupTests(bigDecimalMinimum))
  }

  test("dual is also a semigroup") {
    check(SemigroupTests(Semigroup.dual(intMaximum)))
  }

}
