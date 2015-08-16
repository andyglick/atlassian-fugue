package com.atlassian


import java.util.function.{Function, BiFunction}

/**
 *
 */
package object fugue {

  implicit def Function1Function[A, B](g: A => B): Function[A, B] = new Function[A, B] {
    def apply(a: A) = g(a)
  }

  implicit def Function1Function1FunctionFunction[A, B, C](g: A => B => C) =  new Function[A, Function[B, C]] {
    def apply(a: A) = g(a)
  }

  implicit def Function2BiFunction[A, B, C](g: (A, B) => C): BiFunction[A, B, C] = new BiFunction[A, B, C] {
    def apply(a: A, b: B) = g(a, b)
  }

}
