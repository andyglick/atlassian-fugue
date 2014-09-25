/*
   Copyright 2011 Atlassian

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

import java.util.Date

import scala.util.control.Exception.catching

import org.junit.Assert.{ assertEquals, assertSame, assertTrue }
import org.junit.Test

import com.google.common.base.{ Function => GuavaFunction, Supplier }

class ScalaConvertersTest {

  import ScalaConverters._

  @Test def fromSupplierWithTypeConvertedExplicitly() {
    lazy val converted: () => Int = (Suppliers.ofInstance(1.asInstanceOf[Integer])).asScala
    assertEquals(1, converted())
  }

  @Test def fromSupplierLazily() {
    lazy val converted: () => String = Suppliers.fromOption[String](Option.none()).asScala
    val either: scala.Either[Throwable, String] = catching(classOf[NoSuchElementException]) either {
      converted()
    }
    assertTrue(either.isLeft)
    assertTrue(either.left.get.isInstanceOf[NoSuchElementException])
  }

  @Test def function1ToGuavaFunctionImplicitly() {
    val g: GuavaFunction[String, Integer] = { (s: String) => s.length }.asJava
    assertEquals(3, g.apply("abc"))
  }

  @Test def function1ToGuavaFunctionTypeConvertedExplicitly() {
    val g: GuavaFunction[Integer, Integer] = ((i: Int) => i * 2).asJava
    assertEquals(4, g.apply(2))
  }

  @Test def function1ToGuavaFunctionTypeConvertedImplicitly() {
    def f(ff: GuavaFunction[Integer, Integer]): Integer = ff.apply(2)
    val g: GuavaFunction[Integer, Integer] = ((i: Int) => i * 2).asJava
    assertEquals(4, f(g))
  }

  @Test def guavaFunctionToFunction1Implicitly() {
    val f: (Int => Int) = new GuavaFunction[Integer, Integer] {
      def apply(input: Integer): Integer = input + 1
    }.asScala
    assertEquals(3, f(2))
  }

  @Test def guavaFunctionToFunction1TypeConvertedExplicitly() {
    val f: (Int => Int) = (new GuavaFunction[Integer, Integer] {
      def apply(input: Integer): Integer = input + 1
    }).asScala
    assertEquals(3, f(2))
  }

  @Test def function2ToFugueFunction2Implicitly() {
    val f: Function2[String, Integer, String] = { (s: String, i: Int) => s * i }.asJava
    assertEquals("abcabc", f.apply("abc", 2))
  }

  @Test def function2ToFugueFunction2TypeConvertedExplicitly() {
    val f: Function2[String, Integer, String] = { (s: String, i: Int) => s * i }.asJava
    assertEquals("abcabc", f.apply("abc", 2))
  }

  @Test def function2ToFugueFunction2TypeConvertedImplicitly() {
    val f: Function2[String, Integer, String] = { (s: String, i: Int) => s * i }.asJava
    def g(ff: Function2[String, Integer, String]): String = ff.apply("abc", 2)
    assertEquals("abcabc", g(f))
  }

  @Test def fugueFunction2ToFunction2Implicitly() {
    val f: (String, Int) => String = new Function2[String, Integer, String] {
      def apply(s: String, i: Integer): String = s * i
    }.asScala
    assertEquals("abcabc", f("abc", 2))
  }

  @Test def fugueFunction2ToFunction2TypeConvertedExplicitly() {
    val f = (new Function2[String, Integer, String] {
      def apply(s: String, i: Integer): String = s * i
    }).asScala
    assertEquals("abcabc", f("abc", 2))
  }

  @Test def optionToFugueOption() {
    val o: Option[Integer] = Some(1).asInstanceOf[scala.Option[Int]].asJava
    assertEquals(Option.some(1), o)
  }

  @Test def fugueOptionToOption() {
    val o: scala.Option[Int] = Option.some(Integer.valueOf(1)).asScala
    assertEquals(Some(1), o)
  }

  @Test def eitherToFugueEitherTypeConvertedExplicitly() {
    val e: Either[String, Integer] = Right(1).asInstanceOf[scala.Either[String, Int]].asJava
    assertEquals(Either.right[String, Integer](1), e)
  }

  @Test def tuple2ToFuguePairImplicitly() {
    val p: Pair[String, Integer] = ("abc", 1).asJava
    assertEquals("abc", p.left())
    assertEquals(1, p.right())
  }

  @Test def fuguePairToTuple2() {
    val (s, i) = Pair.pair("abc", Integer.valueOf(1)).asScala
    assertEquals("abc", s)
    assertEquals(1, i)
  }

  @Test def convertSameType() {
    val (d1, d2) = Pair.pair(new Date(1), new Date(2)).asScala
    assertEquals(new Date(1), d1)
    assertEquals(new Date(2), d2)
  }
}
