package io.atlassian.fugue.optic

import io.atlassian.fugue.TestSuite
import io.atlassian.fugue.optic.law.{ OptionalTests, PrismTests, SetterTests, TraversalTests }
import io.atlassian.fugue.optic.std.EitherOptics.right

class PrismSpec extends TestSuite {

  test("Prism Laws") {
    PrismTests(right[String, Int]).check()
  }

  test("Prism.asOptional") {
    OptionalTests(right[String, Int].asOptional()).check()
  }

  test("Prism.asTraversal") {
    TraversalTests(right[String, Int].asTraversal()).check()
  }

  test("Prism.asSetter") {
    SetterTests(right[String, Int].asSetter()).check()
  }

  // TODO: test compose methods
}