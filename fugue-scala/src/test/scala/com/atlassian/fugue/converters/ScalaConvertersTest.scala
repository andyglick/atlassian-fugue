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
import com.google.common.base.Function
import com.google.common.base.Predicate
import org.hamcrest.Matchers.is
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test

import scala.util.control.Exception.catching

class ScalaConvertersTest {

  import ScalaConverters._

  @Test def fromSupplierWithTypeConvertedExplicitly {
    lazy val converted: () => Int = (Suppliers.ofInstance(1.asInstanceOf[Integer])).toScala
    assertThat(1, is(converted()))
  }

  @Test def fromSupplierLazily {
    lazy val converted: () => String = Suppliers.fromOption[String](fugue.Option.none()).toScala
    val either: scala.Either[Throwable, String] = catching(classOf[NoSuchElementException]) either {
      converted()
    }
    assertTrue(either.isLeft)
    assertTrue(either.left.get.isInstanceOf[NoSuchElementException])
  }

  @Test def function1ToGuavaFunctionImplicitly {
    val g: Function[String, Integer] = { (s: String) => s.length }.toJava
    assertThat(new Integer(3), is(g.apply("abc")))
  }

  @Test def function1ToGuavaFunctionTypeConvertedExplicitly {
    val g: Function[Integer, Integer] = ((i: Int) => i * 2).toJava
    assertThat(new Integer(4), is(g.apply(2)))
  }

  @Test def function1ToGuavaFunctionTypeConvertedImplicitly {
    def f(ff: Function[Integer, Integer]): Integer = ff.apply(2)
    val g: Function[Integer, Integer] = ((i: Int) => i * 2).toJava
    assertThat(new Integer(4), is(f(g)))
  }

  @Test def guavaFunctionToFunction1Implicitly {
    val f: (Int => Int) = new Function[Integer, Integer] {
      def apply(input: Integer): Integer = input + 1
    }.toScala
    assertThat(3, is(f(2)))
  }

  @Test def guavaPredicateToFunction1Implicitly {
    val f: (Int => Boolean) = new Predicate[Integer] {
      def apply(input: Integer): Boolean = input % 2 == 0
    }.toScala
    assertThat(f(2), is(true))
    assertThat(f(3), is(false))
  }

  @Test def guavaFunctionToFunction1TypeConvertedExplicitly {
    val f: (Int => Int) = (new Function[Integer, Integer] {
      def apply(input: Integer): Integer = input + 1
    }).toScala
    assertThat(3, is(f(2)))
  }

  @Test def function2ToFugueFunction2Implicitly {
    val f: fugue.Function2[String, Integer, String] = { (s: String, i: Int) => s * i }.toJava
    assertThat("abcabc", is(f.apply("abc", 2)))
  }

  @Test def function2ToFugueFunction2TypeConvertedExplicitly {
    val f: fugue.Function2[String, Integer, String] = { (s: String, i: Int) => s * i }.toJava
    assertThat("abcabc", is(f.apply("abc", 2)))
  }

  @Test def function2ToFugueFunction2TypeConvertedImplicitly {
    val f: fugue.Function2[String, Integer, String] = { (s: String, i: Int) => s * i }.toJava
    def g(ff: fugue.Function2[String, Integer, String]): String = ff.apply("abc", 2)
    assertThat("abcabc", is(g(f)))
  }

  @Test def fugueFunction2ToFunction2Implicitly {
    val f: (String, Int) => String = new fugue.Function2[String, Integer, String] {
      def apply(s: String, i: Integer): String = s * i
    }.toScala
    assertThat("abcabc", is(f("abc", 2)))
  }

  @Test def fugueFunction2ToFunction2TypeConvertedExplicitly {
    val f = (new fugue.Function2[String, Integer, String] {
      def apply(s: String, i: Integer): String = s * i
    }).toScala
    assertThat("abcabc", is(f("abc", 2)))
  }

  @Test def optionToFugueOption {
    val o: fugue.Option[Integer] = Some(1).asInstanceOf[scala.Option[Int]].toJava
    assertThat(fugue.Option.some(new Integer(1)), is(o))
  }

  @Test def fugueOptionToOption {
    val o: scala.Option[Int] = fugue.Option.some(Integer.valueOf(1)).toScala
    assertThat(Some(1), is(o))
  }

  @Test def eitherToFugueEitherTypeConvertedExplicitly {
    val e: fugue.Either[String, Integer] = (Right(1): scala.Either[String, Int]).toJava
    assertThat(fugue.Either.right[String, Integer](1), is(e))
  }

  @Test def tuple2ToFuguePairImplicitly {
    val p: fugue.Pair[String, Integer] = ("abc", 1).toJava
    assertThat("abc", is(p.left))
    assertThat(new Integer(1), is(p.right))
  }

  @Test def fuguePairToTuple2 {
    val (s, i) = fugue.Pair.pair("abc", Integer.valueOf(1)).toScala
    assertThat("abc", is(s))
    assertThat(1, is(i))
  }

  @Test def convertSameType {
    val (d1, d2) = fugue.Pair.pair(new Date(1), new Date(2)).toScala[(Date, Date)]
    assertThat(new Date(1), is(d1))
    assertThat(new Date(2), is(d2))
  }

  @Test def complexToScala {
    val j = new Function[fugue.Pair[Integer, String], fugue.Option[Integer]] {
      def apply(p: fugue.Pair[Integer, String]) = fugue.Option.some(3.toJava)
    }
    // should infer: ((Int, String)) => scala.Option[Int]
    val s = j.toScala
    assertThat(s(2, ""), is(scala.Option(3)))
  }

  @Test def complexToJava {
    val s: ((Int, String)) => scala.Option[Int] = { case (a, b) => scala.Option(3) }
    // should infer: Function[Pair[Integer, String], Option[Integer]]
    val j = s.toJava
    assertThat(j(fugue.Pair.pair(2, "")), is(fugue.Option.option(3.toJava)))
  }
}
