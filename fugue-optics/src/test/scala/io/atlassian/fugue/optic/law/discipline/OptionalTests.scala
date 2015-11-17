package io.atlassian.fugue.optic.law.discipline

import io.atlassian.fugue.optic.POptional
import io.atlassian.fugue.optic.law.OptionalLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

object OptionalTests extends Laws {

  def apply[S: Arbitrary, A: Arbitrary](optional: POptional[S, S, A, A]): RuleSet = {
    val laws: OptionalLaws[S, A] = new OptionalLaws(optional)
    new SimpleRuleSet("Optional",
      "set what you get" -> forAll((s: S) => laws.getOptionSet(s)),
      "get what you set" -> forAll((s: S, a: A) => laws.setGetOption(s, a)),
      "set idempotent" -> forAll((s: S, a: A) => laws.setIdempotent(s, a)),
      "modify id = id" -> forAll((s: S) => laws.modifyIdentity(s)),
      "modifyEitherF point = point" -> forAll((s: S) => laws.modifyEitherFPoint(s)),
      "modifyFunctionF point = point" -> forAll((s: S) => laws.modifyFunctionFPoint(s)),
      "modifyIterableF point = point" -> forAll((s: S) => laws.modifyIterableFPoint(s)),
      "modifyOptionF point = point" -> forAll((s: S) => laws.modifyOptionFPoint(s)),
      "modifyPairF point = point" -> forAll((s: S) => laws.modifyPairFPoint(s)),
      "modifySupplierF point = point" -> forAll((s: S) => laws.modifySupplierFPoint(s)),
      "setOption" -> forAll((s: S, a: A) => laws.setOption(s, a)),
      "modifyOption" -> forAll((s: S) => laws.modifyOptionIdentity(s))
    )
  }

}
