package com.atlassian.fugue.optic

import com.atlassian.fugue.optic.law.discipline.{SetterTests, TraversalTests, OptionalTests, PrismTests}
import com.atlassian.fugue.optic.std.EitherOptics
import com.atlassian.fugue.optic.std.EitherOptics.right

class PrismSpec extends OpticSuite {


  checkAll("apply Prism", PrismTests(right[String, Int]))

  checkAll("prism.asTraversal", OptionalTests(right[String, Int].asOptional))
  checkAll("prism.asTraversal", TraversalTests(right[String, Int].asTraversal))
  checkAll("prism.asSetter"   , SetterTests(right[String, Int].asSetter))


}