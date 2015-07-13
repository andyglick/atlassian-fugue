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
package com.atlassian.fugue.converters

import java.util.Date

import com.atlassian.fugue
import com.atlassian.fugue.Suppliers
import org.hamcrest.Matchers.is
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test

import java.util.function.{BiFunction => JFunction2, Function => JFunction, Predicate => JPredicate, Supplier => JSupplier}

import scala.util.control.Exception.catching

class ScalaConvertersTest {

  import ScalaConverters._

  @Test def fromSupplierWithTypeConvertedExplicitly() {
    lazy val converted: () => Int = Suppliers.ofInstance(1.asInstanceOf[Integer]).asScala
    assertThat(1, is(converted()))
  }

  @Test def fromSupplierLazily() {
    lazy val converted: () => String = Suppliers.fromOption[String](fugue.Option.none()).asScala
    val either: scala.Either[Throwable, String] = catching(classOf[NoSuchElementException]) either {
      converted()
    }
    assertTrue(either.isLeft)
    assertTrue(either.left.get.isInstanceOf[NoSuchElementException])
  }

  @Test def function1ToGuavaFunctionImplicitly() {
    val g: JFunction[String, Integer] = { (s: String) => s.length }.asJava
    assertThat(new Integer(3), is(g.apply("abc")))
  }

  @Test def function1ToGuavaFunctionTypeConvertedExplicitly() {
    val g: JFunction[Integer, Integer] = ((i: Int) => i * 2).asJava
    assertThat(new Integer(4), is(g.apply(2)))
  }

  @Test def function1ToGuavaFunctionTypeConvertedImplicitly() {
    def f(ff: JFunction[Integer, Integer]): Integer = ff.apply(2)
    val g: JFunction[Integer, Integer] = ((i: Int) => i * 2).asJava
    assertThat(new Integer(4), is(f(g)))
  }

  @Test def guavaFunctionToFunction1Implicitly() {
    val f: JFunction[Integer, Integer] = new JFunction[Integer, Integer] {
      override def apply(input: Integer): Integer = input + 1
    }
    val g = f.asScala
    assertThat(3, is(g(2)))
  }

  @Test def guavaPredicateToFunction1Implicitly() {
    val f: JPredicate[Integer] = new JPredicate[Integer] {
      override def test(input: Integer): Boolean = input % 2 == 0
    }
    val g = f.asScala
    assertThat(g(2), is(true))
    assertThat(g(3), is(false))
  }

  @Test def guavaFunctionToFunction1TypeConvertedExplicitly() {
    val f: JFunction[Integer, Integer] = new JFunction[Integer, Integer] {
      def apply(input: Integer): Integer = input + 1
    }
    val ff = f.asScala
    assertThat(3, is(ff(2)))
  }

  @Test def function2ToJFunction2Implicitly() {
    val f: JFunction2[String, Integer, String] = { (s: String, i: Int) => s * i }.asJava
    assertThat("abcabc", is(f.apply("abc", 2)))
  }

  @Test def function2ToJFunction2TypeConvertedExplicitly() {
    val f: JFunction2[String, Integer, String] = { (s: String, i: Int) => s * i }.asJava
    assertThat("abcabc", is(f.apply("abc", 2)))
  }

  @Test def function2ToJFunction2TypeConvertedImplicitly() {
    val f: JFunction2[String, Integer, String] = { (s: String, i: Int) => s * i }.asJava
    def g(ff: JFunction2[String, Integer, String]): String = ff.apply("abc", 2)
    assertThat("abcabc", is(g(f)))
  }

  @Test def JFunction2ToFunction2Implicitly() {
    val f: JFunction2[String, Integer, String] = new JFunction2[String, Integer, String] {
      def apply(s: String, i: Integer): String = s * i
    }
    val g: (String, Int) => String = f.asScala
    assertThat("abcabc", is(g("abc", 2)))
  }

  @Test def JFunction2ToFunction2TypeConvertedExplicitly() {
    val f: JFunction2[String, Integer, String] = new JFunction2[String, Integer, String] {
      def apply(s: String, i: Integer): String = s * i
    }
    val g: (String, Int) => String = f.asScala
    assertThat("abcabc", is(g("abc", 2)))
  }

  @Test def optionToFugueOption() {
    val o: fugue.Option[Integer] = Some(1).asInstanceOf[scala.Option[Int]].asJava
    assertThat(fugue.Option.some(new Integer(1)), is(o))
  }

  @Test def fugueOptionToOption() {
    val o: scala.Option[Int] = fugue.Option.some(Integer.valueOf(1)).asScala
    assertThat(Some(1), is(o))
  }

  @Test def eitherToFugueEitherTypeConvertedExplicitly() {
    val e: fugue.Either[String, Integer] = (Right(1): scala.Either[String, Int]).asJava
    assertThat(fugue.Either.right[String, Integer](1), is(e))
  }

  @Test def tuple2ToFuguePairImplicitly() {
    val p: fugue.Pair[String, Integer] = ("abc", 1).asJava
    assertThat("abc", is(p.left))
    assertThat(new Integer(1), is(p.right))
  }

  @Test def fuguePairToTuple2() {
    val (s, i) = fugue.Pair.pair("abc", Integer.valueOf(1)).asScala
    assertThat("abc", is(s))
    assertThat(1, is(i))
  }

  @Test def convertSameType() {
    val (d1, d2) = fugue.Pair.pair(new Date(1), new Date(2)).asScala[(Date, Date)]
    assertThat(new Date(1), is(d1))
    assertThat(new Date(2), is(d2))
  }

  @Test def complexToScala() {
    val j = new JFunction[fugue.Pair[Integer, String], fugue.Option[Integer]] {
      def apply(p: fugue.Pair[Integer, String]) = fugue.Option.some(p.left() + 1)
    }
    // should infer: ((Int, String)) => scala.Option[Int]
    val s = j.asScala[((Int, String)) => scala.Option[Integer]]
    assertThat(s((2, "")), is(scala.Option(3.asJava)))
  }

  @Test def complexToJava() {
    val s: ((Int, String)) => scala.Option[Int] = { case (a, b) => scala.Option(3) }
    // should infer: JFunction[Pair[Integer, String], Option[Integer]]
    val j = s.asJava
    assertThat(j(fugue.Pair.pair(2.asJava, "")), is(fugue.Option.option(3.asJava)))
  }

}
