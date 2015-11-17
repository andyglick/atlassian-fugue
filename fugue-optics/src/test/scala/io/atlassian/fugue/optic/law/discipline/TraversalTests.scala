package io.atlassian.fugue.optic.law.discipline

import io.atlassian.fugue.optic.PTraversal
import io.atlassian.fugue.optic.law.TraversalLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

object TraversalTests extends Laws {

  def apply[S: Arbitrary, A: Arbitrary](traversal: PTraversal[S, S, A, A]): RuleSet = {
    val laws: TraversalLaws[S, A] = new TraversalLaws(traversal)
    new SimpleRuleSet("Traversal",
      "get what you set" -> forAll((s: S, a: A) => laws.setGetAll(s, a)),
      "set idempotent" -> forAll((s: S, a: A) => laws.setIdempotent(s, a)),
      "modify id = id" -> forAll((s: S) => laws.modifyIdentity(s)),
      "modifyEitherF point = point" -> forAll((s: S) => laws.modifyEitherFPoint(s)),
      "modifyFunctionF point = point" -> forAll((s: S) => laws.modifyFunctionFPoint(s)),
      "modifyIterableF point = point" -> forAll((s: S) => laws.modifyIterableFPoint(s)),
      "modifyOptionF point = point" -> forAll((s: S) => laws.modifyOptionFPoint(s)),
      "modifyPairF point = point" -> forAll((s: S) => laws.modifyPairFPoint(s)),
      "modifySupplierF point = point" -> forAll((s: S) => laws.modifySupplierFPoint(s)),
      "headOption" -> forAll((s: S) => laws.headOption(s))
    )
  }

}
