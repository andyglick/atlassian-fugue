package io.atlassian.fugue.optic.law.discipline

import io.atlassian.fugue.optic.PLens
import io.atlassian.fugue.optic.law.LensLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

object LensTests extends Laws {

  def apply[S: Arbitrary, A: Arbitrary](lens: PLens[S, S, A, A]): RuleSet = {
    val laws: LensLaws[S, A] = new LensLaws(lens)
    new SimpleRuleSet("Lens",
      "set what you get" -> forAll((s: S) => laws.getSet(s)),
      "get what you set" -> forAll((s: S, a: A) => laws.setGet(s, a)),
      "set idempotent" -> forAll((s: S, a: A) => laws.setIdempotent(s, a)),
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
