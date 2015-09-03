package com.atlassian.fugue

import org.scalatest.prop.Checkers
import org.scalatest.{ FunSuite, Matchers }

trait TestSuite extends FunSuite with Checkers with Matchers with TestInstances

