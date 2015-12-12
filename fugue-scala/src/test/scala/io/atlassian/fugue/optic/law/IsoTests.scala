package io.atlassian.fugue.optic.law

import io.atlassian.fugue.optic.PIso
import org.scalacheck.Prop._
import org.scalacheck.{ Properties, Arbitrary }

object IsoTests {

  def apply[S: Arbitrary, A: Arbitrary](iso: PIso[S, S, A, A]) = new Properties("iso") {
    val laws = new IsoLaws(iso)

    property("round trip one way") = forAll((s: S) => laws.roundTripOneWay(s))
    property("round trip other way") = forAll((a: A) => laws.roundTripOtherWay(a))
    property("set") = forAll((s: S, a: A) => laws.set(s, a))
    property("modify id = id") = forAll((s: S) => laws.modifyIdentity(s))
    property("modifyEitherF point = point") = forAll((s: S) => laws.modifyEitherFPoint(s))
    property("modifyFunctionF point = point") = forAll((s: S) => laws.modifyFunctionFPoint(s))
    property("modifyIterableF point = point") = forAll((s: S) => laws.modifyIterableFPoint(s))
    property("modifyOptionF point = point") = forAll((s: S) => laws.modifyOptionFPoint(s))
    property("modifyPairF point = point") = forAll((s: S) => laws.modifyPairFPoint(s))
    property("modifySupplierF point = point") = forAll((s: S) => laws.modifySupplierFPoint(s))
  }

}