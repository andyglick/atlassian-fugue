package io.atlassian.fugue.optic.law

import io.atlassian.fugue.optic.PPrism
import org.scalacheck.Prop._
import org.scalacheck.{ Properties, Arbitrary }

object PrismTests {

  def apply[S: Arbitrary, A: Arbitrary](prism: PPrism[S, S, A, A]) = new Properties("prism") {
    val laws: PrismLaws[S, A] = new PrismLaws(prism)

    property("partial round trip one way") = forAll((s: S) => laws.partialRoundTripOneWay(s))
    property("round trip other way") = forAll((a: A) => laws.roundTripOtherWay(a))
    property("modify id = id") = forAll((s: S) => laws.modifyIdentity(s))
    property("modifyEitherF point = point") = forAll((s: S) => laws.modifyEitherFPoint(s))
    property("modifyFunctionF point = point") = forAll((s: S) => laws.modifyFunctionFPoint(s))
    property("modifyIterableF point = point") = forAll((s: S) => laws.modifyIterableFPoint(s))
    property("modifyOptionF point = point") = forAll((s: S) => laws.modifyOptionFPoint(s))
    property("modifyPairF point = point") = forAll((s: S) => laws.modifyPairFPoint(s))
    property("modifySupplierF point = point") = forAll((s: S) => laws.modifySupplierFPoint(s))
    property("setOption") = forAll((s: S, a: A) => laws.setOption(s, a))
    property("modifyOption") = forAll((s: S) => laws.modifyOptionIdentity(s))

  }

}
