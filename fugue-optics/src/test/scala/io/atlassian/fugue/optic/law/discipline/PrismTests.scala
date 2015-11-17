package io.atlassian.fugue.optic.law.discipline

import io.atlassian.fugue.optic.PPrism
import io.atlassian.fugue.optic.law.PrismLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

object PrismTests extends Laws {

  def apply[S: Arbitrary, A: Arbitrary](prism: PPrism[S, S, A, A]): RuleSet = {
    val laws: PrismLaws[S, A] = new PrismLaws(prism)
    new SimpleRuleSet("Prism",
      "partial round trip one way" -> forAll((s: S) => laws.partialRoundTripOneWay(s)),
      "round trip other way" -> forAll((a: A) => laws.roundTripOtherWay(a)),
      "modify id = id" -> forAll((s: S) => laws.modifyIdentity(s)),
      "modifyEitherF point = point" -> forAll((s: S) => laws.modifyEitherFPoint(s)),
      "modifyFunctionF point = point" -> forAll((s: S) => laws.modifyFunctionFPoint(s)),
      "modifyIterableF point = point" -> forAll((s: S) => laws.modifyIterableFPoint(s)),
      "modifyOptionF point = point" -> forAll((s: S) => laws.modifyOptionFPoint(s)),
      "modifyPairF point = point" -> forAll((s: S) => laws.modifyPairFPoint(s)),
      "modifyStreamF point = point" -> forAll((s: S) => laws.modifyStreamFPoint(s)),
      "modifySupplierF point = point" -> forAll((s: S) => laws.modifySupplierFPoint(s)),
      "setOption" -> forAll((s: S, a: A) => laws.setOption(s, a)),
      "modifyOption" -> forAll((s: S) => laws.modifyOptionIdentity(s))
    )
  }

}
