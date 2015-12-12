package io.atlassian.fugue.optic

import io.atlassian.fugue.Option.{ some, none }
import io.atlassian.fugue._
import org.scalacheck.Arbitrary._
import org.scalacheck.{ Arbitrary, Gen }
import org.scalactic.Equality

trait TestInstances {

  // Arbitrary instances

  implicit def optionArbitrary[A: Arbitrary]: Arbitrary[Option[A]] = Arbitrary(Gen.frequency(
    1 -> none[A](),
    3 -> Arbitrary.arbitrary[A].map(some(_))
  ))

  implicit def eitherArbitrary[A: Arbitrary, B: Arbitrary]: Arbitrary[Either[A, B]] =
    Arbitrary(arbitrary[scala.Either[A, B]] map (e => e.fold(Either.left(_), Either.right(_))))

}
