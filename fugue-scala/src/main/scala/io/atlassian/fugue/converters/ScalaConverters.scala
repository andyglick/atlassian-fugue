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
package io.atlassian.fugue.converters

import java.lang.{ Boolean => JBool, Byte => JByte, Double => JDouble, Float => JFloat, Long => JLong, Short => JShort }
import java.util.function.{ Function => JFunction, BiFunction => JFunction2, Supplier => JSuppiler, Predicate => JPredicate }

import annotation.implicitNotFound

import io.atlassian.fugue

/**
 * Useful for converting Fugue types to Scala and vice-versa.
 *
 * to use, simply `import ScalaConverters._` and then add `.toScala` and `.toJava` as required.
 *
 * Note: that the Fugue side will have Java types such as `java.lang.Integer` and the Scala
 * side will have the Scala equivalents such as `Int`. It will pass reference types though unchanged.
 *
 * Also note that a `Function[Pair[A, B], C]` converts to an `((A, B)) => C` – note the inner parens,
 * it converts to a tupled (1 arg that is a tuple) function. You can turn that into an
 * `(A, B) => C` with `scala.Function.untupled _'
 *
 * Note: The class was moved from package io.atlassian.fugue to io.atlassian.fugue.converters in 2.4, where
 * 'toScala', 'toJava' has been replaced by 'toScala', 'toJava'
 *
 * @since 2.2
 */
object ScalaConverters extends LowPriorityConverters {
  import Iso.<~>

  implicit class ToJavaSyntax[A](val a: A) extends AnyVal {
    def toJava[B](implicit iso: B <~> A): B = iso asA a
  }

  implicit class ToScalaSyntax[A](val a: A) extends AnyVal {
    def toScala[B](implicit iso: A <~> B): B = iso asB a
  }

  implicit val IntIso = Iso[Integer, Int](identity)(identity)
  implicit val LongIso = Iso[JLong, Long](identity)(identity)
  implicit val BoolIso = Iso[JBool, Boolean](identity)(identity)
  implicit val CharacterIso = Iso[Character, Char](identity)(identity)
  implicit val ByteIso = Iso[JByte, Byte](identity)(identity)
  implicit val ShortIso = Iso[JShort, Short](identity)(identity)
  implicit val FloatIso = Iso[JFloat, Float](identity)(identity)
  implicit val DoubleIso = Iso[JDouble, Double](identity)(identity)
  implicit val VoidIso = Iso[Void, Unit] { _ => () } { _ => null }
  implicit val UnitIso = Iso[fugue.Unit, Unit] { _ => () } { _ => fugue.Unit.VALUE }

  implicit def SupplierIso[A, AA](implicit ev: A <~> AA): <~>[JSuppiler[A], () => AA] =
    Iso[JSuppiler[A], () => AA] {
      a => () => a.get.toScala
    } {
      a => new JSuppiler[A] { def get = a().toJava }
    }

  implicit def FunctionIso[A, AA, B, BB](implicit eva: A <~> AA, evb: B <~> BB): Iso[JFunction[A, B], AA => BB] =
    Iso[JFunction[A, B], AA => BB] {
      f => a => f(a.toJava).toScala
    } {
      f => new JFunction[A, B] { def apply(a: A): B = f(a.toScala).toJava }
    }

  implicit def Function2Iso[A, AA, B, BB, C, CC](implicit ia: A <~> AA, ib: B <~> BB, ic: C <~> CC): <~>[JFunction2[A, B, C], (AA, BB) => CC] =
    Iso[JFunction2[A, B, C], (AA, BB) => CC] {
      f => { case (a, b) => f(a.toJava, b.toJava).toScala }
    } {
      f => new JFunction2[A, B, C] { def apply(a: A, b: B): C = f(a.toScala, b.toScala).toJava }
    }

  implicit def PredicateIso[A, AA](implicit eva: A <~> AA): <~>[JPredicate[A], (AA) => Boolean] =
    Iso[JPredicate[A], AA => Boolean] {
      f => a => f.test(a.toJava)
    } {
      f => new JPredicate[A] { def test(a: A): Boolean = f(a.toScala) }
    }

  implicit def OptionIso[A, B](implicit i: A <~> B): Iso[fugue.Option[A], scala.Option[B]] =
    Iso[fugue.Option[A], scala.Option[B]] {
      o => if (o.isEmpty) None else Some(o.get.toScala)
    } {
      o => o.fold(fugue.Option.none[A])(b => fugue.Option.some(b.toJava))
    }

  implicit def EitherIso[A, AA, B, BB](implicit ia: A <~> AA, ib: B <~> BB): <~>[fugue.Either[A, B], scala.Either[AA, BB]] =
    Iso[fugue.Either[A, B], scala.Either[AA, BB]] {
      _.fold(
        new JFunction[A, scala.Either[AA, BB]] { def apply(a: A) = Left(a.toScala) },
        new JFunction[B, scala.Either[AA, BB]] { def apply(b: B) = Right(b.toScala) }
      )
    } {
      _.fold(a => fugue.Either.left(a.toJava), b => fugue.Either.right(b.toJava))
    }

  implicit def PairIso[A, AA, B, BB](implicit ia: A <~> AA, ib: B <~> BB): Iso[fugue.Pair[A, B], (AA, BB)] =
    Iso[fugue.Pair[A, B], (AA, BB)] {
      p => (p.left.toScala, p.right.toScala)
    } {
      case (a, b) => fugue.Pair.pair(a.toJava, b.toJava)
    }

}

trait LowPriorityConverters {
  import Iso._

  implicit def AnyRefIso[A <: AnyRef] =
    Iso.id[A]
}

/**
 * Isomorphism/Bijection between Java and Scala types.
 *
 * Must be natural and a proper bijection, cannot be partial.
 */
@implicitNotFound(
  msg = """Cannot find Iso instance
  from: ${A} 
    to: ${B} 

– usually this is because Scala can't infer one of the types correctly, try specifying the type parameters directly with: 
    
     toScala[OutType]
     toJava[OutType]
    
  Alternately there may not be an Iso for your type.
    
  If you need to construct one that simply passes the type through to the other side otherwise side use:
    
    implicit val MyTypeIso = Iso.id[MyType]
    """
)
sealed trait Iso[A, B] {
  def asB(a: A): B
  def asA(s: B): A
}

object Iso {
  /**
   * Construct an Iso that passes through the type to be used on both sides
   */
  def id[A] = same[A, A]

  type <~>[A, B] = Iso[A, B]

  def apply[A, B](f: A => B)(g: B => A): A <~> B =
    new (A <~> B) {
      def asB(a: A): B = f(a)
      def asA(b: B): A = g(b)
    }

  def same[A, B](implicit asB: A =:= B, asA: B =:= A) = Iso(asB)(asA)
}
