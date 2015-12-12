package io.atlassian.fugue.optic

import io.atlassian.fugue.TestSuite
import io.atlassian.fugue.optic.law.{ OptionalTests, PrismTests, SetterTests, TraversalTests }
import io.atlassian.fugue.optic.std.EitherOptics.right

class PrismSpec extends TestSuite {

  test("Prism Laws") {
    check(PrismTests(right[String, Int]))
  }

  test("Prism.asOptional") {
    check(OptionalTests(right[String, Int].asOptional()))
  }

  test("Prism.asTraversal") {
    check(TraversalTests(right[String, Int].asTraversal()))
  }

  test("Prism.asSetter") {
    check(SetterTests(right[String, Int].asSetter()))
  }

  // TODO: test compose methods
}