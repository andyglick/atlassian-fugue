package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Predicate3;
import io.atlassian.fugue.extensions.functions.Predicate4;
import io.atlassian.fugue.extensions.functions.Predicate5;
import io.atlassian.fugue.extensions.functions.Predicate6;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

final class TestUtils {

  private TestUtils() {
    // do not instantiate
  }

  static <A> Predicate<A> alwaysTrue() {
    return a -> true;
  }

  static <A, B> BiPredicate<A, B> alwaysTrue2() {
    return (a, b) -> true;
  }

  static <A, B, C> Predicate3<A, B, C> alwaysTrue3() {
    return (a, b, c) -> true;
  }

  static <A, B, C, D> Predicate4<A, B, C, D> alwaysTrue4() {
    return (a, b, c, d) -> true;
  }

  static <A, B, C, D, E> Predicate5<A, B, C, D, E> alwaysTrue5() {
    return (a, b, c, d, e) -> true;
  }

  static <A, B, C, D, E, F> Predicate6<A, B, C, D, E, F> alwaysTrue6() {
    return (a, b, c, d, e, f) -> true;
  }

  static <A> Predicate<A> alwaysFalse() {
    return a -> false;
  }

  static <A, B> BiPredicate<A, B> alwaysFalse2() {
    return (a, b) -> false;
  }

  static <A, B, C> Predicate3<A, B, C> alwaysFalse3() {
    return (a, b, c) -> false;
  }

  static <A, B, C, D> Predicate4<A, B, C, D> alwaysFalse4() {
    return (a, b, c, d) -> false;
  }

  static <A, B, C, D, E> Predicate5<A, B, C, D, E> alwaysFalse5() {
    return (a, b, c, d, e) -> false;
  }

  static <A, B, C, D, E, F> Predicate6<A, B, C, D, E, F> alwaysFalse6() {
    return (a, b, c, d, e, f) -> false;
  }

}
