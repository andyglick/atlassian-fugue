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

import com.atlassian.fugue
import com.google.common.base.{ Function => GuavaFunction, Supplier }
import com.atlassian.fugue.{ Either => FugueEither, Function2 => FugueFunction2, Option => FugueOption, Pair => FuguePair, Unit => FUnit }
import java.lang.{ Boolean => JBool, Byte => JByte, Double => JDouble, Float => JFloat, Long => JLong, Short => JShort }

/**
 * Useful for converting Fugue and Guava types to Scala and vice-versa.
 *
 * to use, simply `import ScalaConverters._` and then add `.toScala` and `.toJava` as required.
 *
 * Note that the Fugue/Guava side will have Java types such as `java.lang.Integer` and the Scala
 * side will have the Scala equivalents such as `Int`. You will not be able to convert unless
 */
object ScalaConverters {
  import Iso._

  implicit class ToJavaSyntax[A](val a: A) extends AnyVal {
    def toJava[B](implicit iso: B <~> A): B = iso toJava a
  }

  implicit class ToScalaSyntax[A](val a: A) extends AnyVal {
    def toScala[B](implicit iso: A <~> B): B = iso toScala a
  }

  implicit val IntIso = Iso[Integer, Int](identity)(identity)
  implicit val LongIso = Iso[JLong, Long](identity)(identity)
  implicit val BoolIso = Iso[JBool, Boolean](identity)(identity)
  implicit val CharacterIso = Iso[Character, Char](identity)(identity)
  implicit val ByteIso = Iso[JByte, Byte](identity)(identity)
  implicit val ShortIso = Iso[JShort, Short](identity)(identity)
  implicit val FloatIso = Iso[JFloat, Float](identity)(identity)
  implicit val DoubleIso = Iso[JDouble, Double](identity)(identity)
  implicit val StringIso = Iso[String, String](identity)(identity)
  implicit val VoidIso = Iso[Void, scala.Unit] { _ => () } { _ => null }
  implicit val UnitIso = Iso[Unit, scala.Unit] { _ => () } { _ => Unit.VALUE }

  implicit def SupplierIso[A, AA](implicit ev: A <~> AA) =
    Iso[Supplier[A], () => AA] {
      a => () => a.get.toScala
    } {
      a => new Supplier[A] { def get = a().toJava }
    }

  implicit def FunctionIso[A, AA, B, BB](implicit eva: A <~> AA, evb: B <~> BB) =
    Iso[GuavaFunction[A, B], AA => BB] {
      f => a => f.apply(a.toJava).toScala
    } {
      f => new GuavaFunction[A, B] { def apply(a: A): B = f(a.toScala).toJava }
    }

  implicit def Function2Iso[A, AA, B, BB, C, CC](implicit ia: A <~> AA, ib: B <~> BB, ic: C <~> CC) =
    Iso[Function2[A, B, C], (AA, BB) => CC] {
      f => { case (a, b) => f.apply(a.toJava, b.toJava).toScala }
    } {
      f => new Function2[A, B, C] { def apply(a: A, b: B): C = f(a.toScala, b.toScala).toJava }
    }

  implicit def OptionIso[A, B](implicit i: A <~> B): Iso[Option[A], scala.Option[B]] =
    Iso[Option[A], scala.Option[B]] {
      o => if (o.isEmpty) None else Some(o.get.toScala)
    } {
      o => o.fold(Option.none[A])(b => Option.some(b.toJava))
    }

  implicit def EitherIso[A, AA, B, BB](implicit ia: A <~> AA, ib: B <~> BB) =
    Iso[Either[A, B], scala.Either[AA, BB]] {
      _.fold(
        new GuavaFunction[A, scala.Either[AA, BB]] { def apply(a: A) = Left(a.toScala) },
        new GuavaFunction[B, scala.Either[AA, BB]] { def apply(b: B) = Right(b.toScala) }
      )
    } {
      _.fold(a => Either.left(a.toJava), b => Either.right(b.toJava))
    }

  implicit def PairIso[A, AA, B, BB](implicit ia: A <~> AA, ib: B <~> BB) =
    Iso[Pair[A, B], (AA, BB)] {
      p => (p.left.toScala, p.right.toScala)
    } {
      case (a, b) => Pair.pair(a.toJava, b.toJava)
    }
}

object Iso {
  /** 
   * Isomorphism/Bijection between Java and Scala types.
   * 
   * Must be natural and a proper bijection, cannot be partial.
   */
  sealed trait Iso[A, S] {
    def toScala(a: A): S
    def toJava(s: S): A
  }

  type <~>[A, B] = Iso[A, B]

  def apply[A, B](f: A => B)(g: B => A): A <~> B =
    new (A <~> B) {
      def toScala(a: A): B = f(a)
      def toJava(b: B): A = g(b)
    }
}