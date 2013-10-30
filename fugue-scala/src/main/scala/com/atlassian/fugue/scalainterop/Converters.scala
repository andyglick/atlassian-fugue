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

import com.atlassian.fugue
import com.google.common.base.{Function => GuavaFunction, Supplier}
import com.atlassian.fugue.{Either => FugueEither, Function2 => FugueFunction2, Option => FugueOption}
import java.lang.{Boolean => JBool, Byte => JByte, Double => JDouble, Float => JFloat, Long => JLong, Short => JShort}

sealed trait Converter[A, B] {
  def convert(a: A): B
}

trait LowPriorityConversions {

  /**
   * Provides an implicitly identity converter. It can also convert a type to a super type, as so
   * {{{
   *   scala> val i: Integer = 1
   *   scala> val s: Number = implicitly[Converter[Integer, Number]].convert(i)
   * }}}
   */
  implicit def AnyRefConverter[A <: AnyRef, AA >: A] = new Converter[A, AA] {
    def convert(a: A) = a
  }
}

object Converters extends LowPriorityConversions {

  implicit object IntConverter extends Converter[Int, Integer] {
    def convert(a: Int): Integer = a
  }

  implicit object IntegerConverter extends Converter[Integer, Int] {
    def convert(a: Integer): Int = a
  }

  implicit object LongConverter extends Converter[Long, JLong] {
    def convert(a: Long): JLong = a
  }

  implicit object JLongConverter extends Converter[JLong, Long] {
    def convert(a: JLong): Long = a
  }

  implicit object BooleanConverter extends Converter[Boolean, JBool] {
    def convert(a: Boolean): JBool = a
  }

  implicit object JBoolConverter extends Converter[JBool, Boolean] {
    def convert(a: JBool): Boolean = a
  }

  implicit object CharConverter extends Converter[Char, Character] {
    def convert(a: Char): Character = a
  }

  implicit object CharacterConverter extends Converter[Character, Char] {
    def convert(a: Character): Char = a
  }

  implicit object ByteConverter extends Converter[Byte, JByte] {
    def convert(a: Byte): JByte = a
  }

  implicit object JByteConverter extends Converter[JByte, Byte] {
    def convert(a: JByte): Byte = a
  }

  implicit object ShortConverter extends Converter[Short, JShort] {
    def convert(a: Short): JShort = a
  }

  implicit object JShortConverter extends Converter[JShort, Short] {
    def convert(a: JShort): Short = a
  }

  implicit object FloatConverter extends Converter[Float, JFloat] {
    def convert(a: Float): JFloat = a
  }

  implicit object JFloatConverter extends Converter[JFloat, Float] {
    def convert(a: JFloat): Float = a
  }

  implicit object DoubleConverter extends Converter[Double, JDouble] {
    def convert(a: Double): JDouble = a
  }

  implicit object JDoubleConverter extends Converter[JDouble, Double] {
    def convert(a: JDouble): Double = a
  }

  implicit object UnitConverter extends Converter[Unit, Void] {
    def convert(a: Unit): Void = null
  }

  implicit object VoidConverter extends Converter[Void, Unit] {
    def convert(a: Void): Unit = ()
  }

  /**
   * Implicitly boxes a lazy evaluated parameter into Guava Supplier. The parameter is evaluated only when the get()
   * method of the result Supplier is called.
   *
   * Note that the type of the parameter is not converted. So toGuavaSupplier(1) returns a Supplier[Int] instead of
   * Supplier[Integer], as so
   * {{{
   *   scala> val s: Supplier[Int] = 1
   *   scala> s.get
   *   res0: Int = 1
   * }}}
   */
  implicit def toGuavaSupplier[A](a: => A): Supplier[A] =
    new Supplier[A] {
      def get = a
    }

  /**
   * Implicitly converts the a Supplier of type A to a Supplier of type AA, given a converter from A to AA exists.
   *
   * This will convert Supplier[Int] to Supplier[Integer], as so
   * {{{
   *   scala> val s: Supplier[Int] = 1
   *   scala> val s1: Supplier[Integer] = s
   *   scala> s1.get
   *   res0: Integer = 1
   * }}}
   */
  implicit def convertSupplier[A, AA](s: Supplier[A])(implicit ev: Converter[A, AA]): Supplier[AA] =
    new Supplier[AA] {
      def get(): AA = ev.convert(s.get())
    }

  /**
   * Explicitly boxes a lazy evaluated parameter into Guava Supplier, with the type of the parameter converted to
   * target type. The parameter is evaluated only when the get() method of the result Supplier is called.
   *
   * {{{
   *   scala> val s: Supplier[Integer] = toSupplier(1)
   *   scala> s.get
   *   res0: Integer = 1
   * }}}
   */
  def toSupplier[A, AA](a: => A)(implicit ev: Converter[A, AA]): Supplier[AA] = convertSupplier(toGuavaSupplier(a))(ev)

  /**
   * Implicitly unboxes a Guava Supplier.
   *
   * Note that the Supplier's get() method is called immediately unless it's assigned to a lazy val or
   * passed to a function expecting a by-name parameter.
   *
   * {{{
   *   scala> lazy val s: Integer = Suppliers.ofInstance(1)
   * }}}
   */
  implicit def fromGuavaSupplier[A](a: Supplier[A]): A = a.get()

  /**
   * Explicitly unboxes a Guava Supplier, with the type of the Supplier converted to the target type.
   * 
   * Note that the Supplier's get() method is called immediately unless it's assigned to a lazy val or
   * passed to a function expecting a by-name parameters.
   * 
   * {{{
   *   scala> lazy val s: Int = fromSupplier(Suppliers.ofInstance(1))
   * }}}
   */
  def fromSupplier[A, AA](s: Supplier[A])(implicit ev: Converter[A, AA]): AA = ev.convert(fromGuavaSupplier(s))

  implicit def toGuavaFunction[A, B](f: A => B): GuavaFunction[A, B] =
    new GuavaFunction[A, B] {
      def apply(a: A) = f(a)
    }

  implicit def convertGuavaFunction[A, B, AA, BB](f: GuavaFunction[A, B])(implicit eva: Converter[AA, A], evb: Converter[B, BB]): GuavaFunction[AA, BB] =
    new GuavaFunction[AA, BB] {
      def apply(aa: AA): BB = evb.convert(f.apply(eva.convert(aa)))
    }

  def fromFunction1[A, B, AA, BB](f: A => B)(implicit eva: Converter[AA, A], evb: Converter[B, BB]): GuavaFunction[AA, BB] =
    convertGuavaFunction(toGuavaFunction(f))(eva, evb)

  implicit def fromGuavaFunction[A, B](f: GuavaFunction[A, B]): (A => B) = f.apply(_)

  def toFunction1[A, B, AA, BB](f: GuavaFunction[A, B])(implicit eva: Converter[AA, A], evb: Converter[B, BB]): (AA => BB) =
    aa => evb.convert(fromGuavaFunction(f)(eva.convert(aa)))

  implicit def toFugueFunction2[A, B, C](f2: (A, B) => C): FugueFunction2[A, B, C] =
    new FugueFunction2[A, B, C] {
      def apply(a: A, b: B): C = f2(a, b)
    }

  implicit def convertFugueFunction2[A, B, C, AA, BB, CC](f: FugueFunction2[A, B, C])(implicit eva: Converter[AA, A], evb: Converter[BB, B], evc: Converter[C, CC]): FugueFunction2[AA, BB, CC] =
    new FugueFunction2[AA, BB, CC] {
      def apply(aa: AA, bb: BB): CC = evc.convert(f.apply(eva.convert(aa), evb.convert(bb)))
    }

  def fromFunction2[A, B, C, AA, BB, CC](f: (A, B) => C)(implicit eva: Converter[AA, A], evb: Converter[BB, B], evc: Converter[C, CC]): FugueFunction2[AA, BB, CC] =
    convertFugueFunction2(toFugueFunction2(f))(eva, evb, evc)

  implicit def fromFugueFunction2[A, B, C](f: FugueFunction2[A, B, C]): ((A, B) => C) = f.apply(_, _)

  def toFunction2[A, B, C, AA, BB, CC](f: FugueFunction2[A, B, C])(implicit eva: Converter[AA, A], evb: Converter[BB, B], evc: Converter[C, CC]): (AA, BB) => CC =
    (aa, bb) => evc.convert(fromFugueFunction2(f)(eva.convert(aa), evb.convert(bb)))

  implicit def toFugueOption[A](o: Option[A]): FugueOption[A] =
    o.fold(FugueOption.none[A]())(a => FugueOption.some(a))

  implicit def convertFugueOption[A, AA](o: FugueOption[A])(implicit ev: Converter[A, AA]): FugueOption[AA] =
    o.fold(FugueOption.none[AA](), ((a: A) => FugueOption.some(ev.convert(a))))

  def fromOption[A, AA](o: Option[A])(implicit ev: Converter[A, AA]): FugueOption[AA] =
    convertFugueOption(toFugueOption(o))(ev)

  implicit def fromFugueOption[A](o: FugueOption[A]): Option[A] = o.fold(None, ((a: A) => Some(a)))

  def toOption[A, AA](o: FugueOption[A])(implicit ev: Converter[A, AA]): Option[AA] =
    fromFugueOption(o).fold(None: Option[AA])(a => Some(ev.convert(a)))

  implicit def toFugueEither[A, B](e: Either[A, B]): FugueEither[A, B] =
    e.fold(a => FugueEither.left(a), b => FugueEither.right(b))

  implicit def convertFugueEither[A, B, AA, BB](e: FugueEither[A, B])(implicit eva: Converter[A, AA], evb: Converter[B, BB]): FugueEither[AA, BB] =
    e.fold(((a: A) => FugueEither.left[AA, BB](eva.convert(a))), ((b: B) => FugueEither.right[AA, BB](evb.convert(b))))

  def fromEither[A, B, AA, BB](e: Either[A, B])(implicit eva: Converter[A, AA], evb: Converter[B, BB]): FugueEither[AA, BB] =
    convertFugueEither(toFugueEither(e))(eva, evb)

  implicit def fromFugueEither[A, B](e: FugueEither[A, B]): Either[A, B] =
    e.fold(((a: A) => Left(a): Either[A, B]), ((b: B) => Right(b): Either[A, B]))

  def toEither[A, B, AA, BB](e: FugueEither[A, B])(implicit eva: Converter[A, AA], evb: Converter[B, BB]): Either[AA, BB] =
    fromFugueEither(e).fold(a => Left(eva.convert(a)), b => Right(evb.convert(b)))

}
