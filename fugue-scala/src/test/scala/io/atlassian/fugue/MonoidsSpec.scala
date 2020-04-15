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

import java.math.BigInteger
import java.util.Arrays.asList
import java.util.stream.{ Collectors, StreamSupport }

import io.atlassian.fugue.Either.right
import Monoids._
import io.atlassian.fugue.Option.some
import io.atlassian.fugue.law.MonoidTests

class MonoidsSpec extends TestSuite {

  test("intAddition") {
    intAddition.append(1, 2) shouldEqual 3
    MonoidTests(intAddition).check()
  }

  test("intMultiplication") {
    intMultiplication.append(2, 3) shouldEqual 6
    MonoidTests(intMultiplication).check()
  }

  test("bigintAddition") {
    bigintAddition.append(BigInteger.valueOf(2), BigInteger.valueOf(3)) shouldEqual BigInteger.valueOf(5)
    MonoidTests(bigintAddition).check()
  }

  test("bigintMultiplication") {
    bigintMultiplication.append(BigInteger.valueOf(2), BigInteger.valueOf(3)) shouldEqual BigInteger.valueOf(6)
    MonoidTests(bigintMultiplication).check()
  }

  test("longAddition") {
    longAddition.append(1L, 2L) shouldEqual 3L
    MonoidTests(longAddition).check()
  }

  test("longMultiplication") {
    longMultiplication.append(2L, 3L) shouldEqual 6L
    MonoidTests(longMultiplication).check()
  }

  test("disjunction") {
    disjunction.append(false, true) shouldEqual true
    disjunction.append(true, true) shouldEqual true
    MonoidTests(disjunction).check()
  }

  test("exclusiveDisjunction") {
    exclusiveDisjunction.append(false, true) shouldEqual true
    exclusiveDisjunction.append(true, true) shouldEqual false
    MonoidTests(exclusiveDisjunction).check()
  }

  test("conjunction") {
    conjunction.append(false, true) shouldEqual false
    conjunction.append(true, true) shouldEqual true
    MonoidTests(conjunction).check()
  }

  test("string") {
    string.append("a", "b") shouldEqual "ab"
    MonoidTests(string).check()
  }

  test("unit") {
    MonoidTests(unit).check()
  }

  test("list") {
    list[String]().append(asList("a"), asList("b")) shouldEqual asList("a", "b")
    MonoidTests(list[Integer]()).check()
  }

  test("iterable") {
    StreamSupport.stream(iterable[String]().append(asList("a"), asList("b")).spliterator(), false).collect(Collectors.toList[String]) shouldEqual asList("a", "b")
  }

  test("firstOption") {
    firstOption[String]().append(some("a"), some("b")) shouldEqual some("a")
    MonoidTests(firstOption[Integer]()).check()
  }

  test("lastOption") {
    lastOption[String]().append(some("a"), some("b")) shouldEqual some("b")
    MonoidTests(lastOption[Integer]()).check()
  }

  test("option") {
    option(Semigroups.intMaximum).append(some(1), some(2)) shouldEqual some(2)
    option(Semigroups.intMaximum).append(some(3), some(2)) shouldEqual some(3)
    MonoidTests(option(Semigroups.intMaximum)).check()
  }

  test("either") {
    val m = either(Semigroups.intMaximum, string)

    m.append(right("a"), right("b")) shouldEqual right("ab")
    m.append(Either.left(1), Either.left(2)) shouldEqual Either.left(2)
    MonoidTests(m).check()
  }

}
