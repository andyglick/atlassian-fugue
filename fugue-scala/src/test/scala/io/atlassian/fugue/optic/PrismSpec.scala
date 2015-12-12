package io.atlassian.fugue.optic

import io.atlassian.fugue.TestSuite
import io.atlassian.fugue.optic.law.{ OptionalTests, PrismTests, SetterTests, TraversalTests }
import io.atlassian.fugue.optic.std.EitherOptics._right

class PrismSpec extends TestSuite {

  test("Prism Laws") {
    check(PrismTests(_right[String, Int]))
  }

  test("Prism.asOptional") {
    check(OptionalTests(_right[String, Int].asOptional()))
  }

  test("Prism.asTraversal") {
    check(TraversalTests(_right[String, Int].asTraversal()))
  }

  test("Prism.asSetter") {
    check(SetterTests(_right[String, Int].asSetter()))
  }

}