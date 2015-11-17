package io.atlassian.fugue.optic

import io.atlassian.fugue.optic.law.discipline.{ SetterTests, TraversalTests, OptionalTests, PrismTests }
import io.atlassian.fugue.optic.std.EitherOptics
import io.atlassian.fugue.optic.std.EitherOptics.right

class PrismSpec extends OpticSuite {

  checkAll("apply Prism", PrismTests(right[String, Int]))

  checkAll("prism.asTraversal", OptionalTests(right[String, Int].asOptional))
  checkAll("prism.asTraversal", TraversalTests(right[String, Int].asTraversal))
  checkAll("prism.asSetter", SetterTests(right[String, Int].asSetter))

}