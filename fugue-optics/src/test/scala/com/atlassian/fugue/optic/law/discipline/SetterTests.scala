package com.atlassian.fugue.optic.law.discipline

import com.atlassian.fugue.optic.PSetter
import com.atlassian.fugue.optic.law.SetterLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

object SetterTests extends Laws {

  def apply[S: Arbitrary, A: Arbitrary](setter: PSetter[S, S, A, A]): RuleSet = {
    val laws: SetterLaws[S, A] = new SetterLaws(setter)
    new SimpleRuleSet("Setter",
      "set idempotent" -> forAll( (s: S, a: A) => laws.setIdempotent(s, a)),
      "modify id = id" -> forAll( (s: S) => laws.modifyIdentity(s))
    )
  }

}
