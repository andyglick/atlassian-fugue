package com.atlassian.fugue

import org.scalatest.{FunSuite, Matchers}
import org.typelevel.discipline.scalatest.Discipline

trait TestSuite extends FunSuite with Discipline with Matchers with TestInstances


