package io.atlassian.fugue.optic.law

import io.atlassian.fugue.optic.PTraversal
import org.scalacheck.Prop._
import org.scalacheck.{ Properties, Arbitrary }

object TraversalTests {

  def apply[S: Arbitrary, A: Arbitrary](traversal: PTraversal[S, S, A, A]) = new Properties("traversal") {
    val laws: TraversalLaws[S, A] = new TraversalLaws(traversal)

    property("get what you set") = forAll((s: S, a: A) => laws.setGetAll(s, a))
    property("set idempotent") = forAll((s: S, a: A) => laws.setIdempotent(s, a))
    property("modify id = id") = forAll((s: S) => laws.modifyIdentity(s))
    property("modifyEitherF point = point") = forAll((s: S) => laws.modifyEitherFPoint(s))
    property("modifyFunctionF point = point") = forAll((s: S) => laws.modifyFunctionFPoint(s))
    property("modifyIterableF point = point") = forAll((s: S) => laws.modifyIterableFPoint(s))
    property("modifyOptionF point = point") = forAll((s: S) => laws.modifyOptionFPoint(s))
    property("modifyPairF point = point") = forAll((s: S) => laws.modifyPairFPoint(s))
    property("modifySupplierF point = point") = forAll((s: S) => laws.modifySupplierFPoint(s))
    property("headOption") = forAll((s: S) => laws.headOption(s))
  }

}
