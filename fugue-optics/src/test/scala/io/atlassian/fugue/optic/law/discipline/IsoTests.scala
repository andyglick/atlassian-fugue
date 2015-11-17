package io.atlassian.fugue.optic.law.discipline

import io.atlassian.fugue.optic.PIso
import io.atlassian.fugue.optic.law.IsoLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

object IsoTests extends Laws {

  def apply[S: Arbitrary, A: Arbitrary](iso: PIso[S, S, A, A]): RuleSet = {
    val laws = new IsoLaws(iso)
    new SimpleRuleSet("Iso",
      "round trip one way" -> forAll((s: S) => laws.roundTripOneWay(s)),
      "round trip other way" -> forAll((a: A) => laws.roundTripOtherWay(a)),
      "set" -> forAll((s: S, a: A) => laws.set(s, a)),
      "modify id = id" -> forAll((s: S) => laws.modifyIdentity(s)),
      "modifyEitherF point = point" -> forAll((s: S) => laws.modifyEitherFPoint(s)),
      "modifyFunctionF point = point" -> forAll((s: S) => laws.modifyFunctionFPoint(s)),
      "modifyIterableF point = point" -> forAll((s: S) => laws.modifyIterableFPoint(s)),
      "modifyOptionF point = point" -> forAll((s: S) => laws.modifyOptionFPoint(s)),
      "modifyPairF point = point" -> forAll((s: S) => laws.modifyPairFPoint(s)),
      "modifySupplierF point = point" -> forAll((s: S) => laws.modifySupplierFPoint(s))
    )
  }

}