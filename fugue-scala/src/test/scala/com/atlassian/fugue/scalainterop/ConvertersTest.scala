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
package com.atlassian.fugue.scalainterop

import java.util.Date

import scala.util.control.Exception.catching

import org.junit.Assert.{ assertEquals, assertSame, assertTrue }
import org.junit.Test

import com.atlassian.fugue.{ Either => FugueEither, Function2 => FugueFunction2, Option => FugueOption, Pair => FuguePair, Suppliers }
import com.google.common.base.{ Function => GuavaFunction, Supplier }

import Converters.{ AnyRefConverter, IntConverter, IntegerConverter, convertFugueEither, convertFugueFunction2, convertFugueOption, convertFuguePair, convertGuavaFunction, convertSupplier, fromEither, fromFugueEither, fromFugueFunction2, fromFugueOption, fromFuguePair, fromFunction1, fromFunction2, fromGuavaFunction, fromGuavaSupplier, fromOption, fromSupplier, fromTuple2, toEither, toFugueEither, toFugueFunction2, toFugueOption, toFuguePair, toFunction1, toFunction2, toGuavaFunction, toGuavaSupplier, toOption, toSupplier, toTuple2 }

class ConvertersTest {

  import Converters._

  @Test def anyRefToAnyRefImplicitly() {
    val s = new Date()
    val t: Date = implicitly[Converter[Date, Date]].convert(s)
    assertSame(s, t)
  }

  @Test def anyRefToAnyRefSuperclassImplicitly() {
    val s: Integer = 1
    val t: Number = implicitly[Converter[Integer, Number]].convert(s)
    assertSame(s, t)
  }

  @Test def toSupplierImplicitly() {
    val s: Supplier[String] = "abc"
    assertEquals("abc", s.get())
  }

  @Test def toSupplierWithTypeConvertedExplicitly() {
    val s: Supplier[Integer] = toSupplier(1)
    assertEquals(1, s.get())
  }

  @Test def toSupplierWithTypeConvertedImplicitly() {
    def f(s: Supplier[Integer]): Integer = s.get + 1
    val s: Supplier[Int] = 1
    // Supplier[Int] is converted to Supplier[Integer] implicitly in call: f(s).
    assertEquals(2, f(s))
  }

  @Test def toSupplierLazily() {
    def constantPrefix(prefix: => String): String => Supplier[String] = { text =>
      // following line implicitly converts the => String to Supplier[String] without evaluation
      // the while expression is also implicitly converted to Supplier[String] as the return value
      prefix.get() + text
    }
    assertEquals("abcdef", constantPrefix("abc")("def").get())
    val supplier: Supplier[String] = constantPrefix(throw new IllegalStateException("Fail"))("def")
    val either: Either[Throwable, String] = catching[String](classOf[IllegalStateException]) either {
      supplier.get()
    }
    assertTrue(either.isLeft)
    assertTrue(either.left.get.isInstanceOf[IllegalStateException])
  }

  @Test def fromSupplierImplicitly() {
    val converted: String = Suppliers.ofInstance("abc")
    assertEquals("abc", converted)
  }

  @Test def fromSupplierWithTypeConvertedExplicitly() {
    lazy val converted: Int = fromSupplier(Suppliers.ofInstance(1))
    assertEquals(1, converted)
  }

  @Test def fromSupplierLazily() {
    lazy val converted: String = Suppliers.fromOption[String](FugueOption.none())
    val either: Either[Throwable, String] = catching(classOf[NoSuchElementException]) either {
      converted
    }
    assertTrue(either.isLeft)
    assertTrue(either.left.get.isInstanceOf[NoSuchElementException])
  }

  @Test def function1ToGuavaFunctionImplicitly() {
    val g: GuavaFunction[String, Int] = (s: String) => s.length
    assertEquals(3, g.apply("abc"))
  }

  @Test def function1ToGuavaFunctionTypeConvertedExplicitly() {
    val g: GuavaFunction[Integer, Integer] = fromFunction1((i: Int) => i * 2)
    assertEquals(4, g.apply(2))
  }

  @Test def function1ToGuavaFunctionTypeConvertedImplicitly() {
    def f(ff: GuavaFunction[Integer, Integer]): Integer = ff.apply(2)
    val g: GuavaFunction[Int, Int] = (i: Int) => i * 2
    assertEquals(4, f(g))
  }

  @Test def guavaFunctionToFunction1Implicitly() {
    val f: (Int => Int) = new GuavaFunction[Int, Int] {
      def apply(input: Int): Int = input + 1
    }
    assertEquals(3, f(2))
  }

  @Test def guavaFunctionToFunction1TypeConvertedExplicitly() {
    val f: (Int => Int) = toFunction1(new GuavaFunction[Integer, Integer] {
      def apply(input: Integer): Integer = input + 1
    })
    assertEquals(3, f(2))
  }

  @Test def function2ToFugueFunction2Implicitly() {
    val f: FugueFunction2[String, Int, String] = (s: String, i: Int) => s * i
    assertEquals("abcabc", f.apply("abc", 2))
  }

  @Test def function2ToFugueFunction2TypeConvertedExplicitly() {
    val f: FugueFunction2[String, Integer, String] = fromFunction2((s: String, i: Int) => s * i)
    assertEquals("abcabc", f.apply("abc", 2))
  }

  @Test def function2ToFugueFunction2TypeConvertedImplicitly() {
    val f: FugueFunction2[String, Int, String] = (s: String, i: Int) => s * i
    def g(ff: FugueFunction2[String, Integer, String]): String = ff.apply("abc", 2)
    assertEquals("abcabc", g(f))
  }

  @Test def fugueFunction2ToFunction2Implicitly() {
    val f: (String, Int) => String = new FugueFunction2[String, Int, String] {
      def apply(s: String, i: Int): String = s * i
    }
    assertEquals("abcabc", f("abc", 2))
  }

  @Test def fugueFunction2ToFunction2TypeConvertedExplicitly() {
    val f: (String, Integer) => String = toFunction2(new FugueFunction2[String, Int, String] {
      def apply(s: String, i: Int): String = s * i
    })
    assertEquals("abcabc", f("abc", 2))
  }

  @Test def optionToFugueOptionImplicitly() {
    val o: FugueOption[Int] = Some(1)
    assertEquals(FugueOption.some(1), o)
  }

  @Test def optionToFugueOptionTypeConvertedExplicitly() {
    val o: FugueOption[Integer] = fromOption(Some(1))
    assertEquals(FugueOption.some(1), o)
  }

  @Test def optionToFugueOptionTypeConvertedImplicitly() {
    val o: FugueOption[Int] = Some(1)
    def g(o: FugueOption[Integer]): Int = o.fold(Suppliers.ofInstance(0), ((i: Integer) => i + 1))
    assertEquals(2, g(o))
  }

  @Test def fugueOptionToOptionImplicitly() {
    val o: Option[Int] = FugueOption.some(1)
    assertEquals(Some(1), o)
  }

  @Test def fugueOptionToOptionTypeConvertedExplicitly() {
    val o: Option[Integer] = toOption(FugueOption.some(1))
    assertEquals(Some(1), o)
  }

  @Test def eitherToFugueEitherImplicitly() {
    val e: FugueEither[String, Int] = Right(1)
    assertEquals(FugueEither.right[String, Int](1), e)
  }

  @Test def eitherToFugueEitherTypeConvertedExplicitly() {
    val e: FugueEither[String, Integer] = fromEither(Right(1))
    assertEquals(FugueEither.right[String, Int](1), e)
  }

  @Test def eitherToFugueEitherTypeConvertedImplicitly() {
    val e: FugueEither[String, Int] = Right(1)
    def f(e: FugueEither[String, Integer]): Int = e.fold(((_: String) => 0), ((i: Integer) => i + 1))
    assertEquals(2, f(e))
  }

  @Test def fugueEitherToEitherImplicitly() {
    val e: Either[String, Int] = FugueEither.right[String, Int](1)
    assertEquals(Right(1), e)
  }

  @Test def fugueEitherToEitherTypeConvertedExplicitly() {
    val e: Either[String, Int] = toEither(FugueEither.right[String, Integer](1))
    assertEquals(Right(1), e)
  }

  @Test def tuple2ToFuguePairImplicitly() {
    val p: FuguePair[String, Int] = ("abc", 1)
    assertEquals("abc", p.left())
    assertEquals(1, p.right())
  }

  @Test def tuple2ToFuguePairTypeConvertedExplicitly() {
    val p: FuguePair[String, Integer] = fromTuple2(("abc", 1))
    assertEquals("abc", p.left())
    assertEquals(1, p.right())
  }

  @Test def tuple2ToFuguePairTypeConvertedImplicitly() {
    val p: FuguePair[String, Int] = ("abc", 1)
    def f(p: FuguePair[String, Integer]): FuguePair[Integer, String] = FuguePair.pair(p.right(), p.left())
    val q: FuguePair[Integer, String] = f(p)
    assertEquals(1, q.left())
    assertEquals("abc", q.right())
  }

  @Test def fuguePairToTuple2Implicitly() {
    val p: (String, Int) = FuguePair.pair("abc", 1)
    assertEquals("abc", p._1)
    assertEquals(1, p._2)
  }

  @Test def fuguePairToTuple2TypeConvertedExplicitly() {
    val (s: String, i: Int) = toTuple2(FuguePair.pair[String, Integer]("abc", 1))
    assertEquals("abc", s)
    assertEquals(1, i)
  }
}
