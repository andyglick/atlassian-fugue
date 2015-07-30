package com.atlassian.fugue

import com.atlassian.fugue.Either.{right, left}
import com.atlassian.fugue.Option.{none, some}
import com.atlassian.fugue.Pair.pair
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}

trait TestInstances {


  // Arbitrary instances

  implicit def optionArbitrary[A: Arbitrary]: Arbitrary[Option[A]] = Arbitrary(Gen.frequency(
    1 -> none[A](),
    3 -> arbitrary[A].map(some(_))
  ))

  implicit def eitherArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[Either[A, B]] =
    Arbitrary(arbitrary[scala.Either[A, B]] map (e => e.fold(left(_), right(_))))

  implicit def pairArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[Pair[A, B]] =
    Arbitrary(arbitrary[scala.Tuple2[A, B]] map (t => pair(t._1, t._2)))

}
