package com.atlassian.fugue

import java.math.BigInteger

import com.atlassian.fugue.Either.{left, right}
import com.atlassian.fugue.Option.{none, some}
import com.atlassian.fugue.Pair.pair
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}

import scala.collection.JavaConversions._


trait TestInstances {


  // Arbitrary instances

  implicit def optionArbitrary[A: Arbitrary]: Arbitrary[Option[A]] = Arbitrary(Gen.frequency(
    1 -> none[A](),
    3 -> arbitrary[A].map(some(_))
  ))

  implicit val integerArbitrary: Arbitrary[Integer] = Arbitrary(arbitrary[Int] map (_.toInt))

  implicit val doubleArbitrary: Arbitrary[java.lang.Double] = Arbitrary(arbitrary[Double] map (_.toDouble))

  implicit val bigIntegerArbitrary: Arbitrary[BigInteger] = Arbitrary(arbitrary[BigInt] map (_.bigInteger))

  implicit val bigDecimalArbitrary: Arbitrary[java.math.BigDecimal] = Arbitrary(arbitrary[BigDecimal] map (_.bigDecimal))

  implicit val longArbitrary: Arbitrary[java.lang.Long] = Arbitrary(arbitrary[Long] map (_.toLong))

  implicit val booleanArbitrary: Arbitrary[java.lang.Boolean] = Arbitrary(arbitrary[Boolean] map (_.booleanValue()))

  implicit val unitArbitrary: Arbitrary[Unit] = Arbitrary(Gen.const(Unit.VALUE))

  implicit def eitherArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[Either[A, B]] =
    Arbitrary(arbitrary[scala.Either[A, B]] map (e => e.fold(left(_), right(_))))

  implicit def pairArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[Pair[A, B]] =
    Arbitrary(arbitrary[scala.Tuple2[A, B]] map (t => pair(t._1, t._2)))

  implicit def javaListArbitrary[A: Arbitrary]: Arbitrary[java.util.List[A]] =
    Arbitrary(arbitrary[scala.List[A]] map seqAsJavaList)

  implicit def javaIterableArbitrary[A: Arbitrary]: Arbitrary[java.lang.Iterable[A]] =
    Arbitrary(arbitrary[scala.List[A]] map seqAsJavaList)

}
