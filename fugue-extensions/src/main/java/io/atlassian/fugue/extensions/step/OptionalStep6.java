package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function6;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public class OptionalStep6<A, B, C, D, E, F> {
  private final Optional<A> optional1;
  private final Optional<B> optional2;
  private final Optional<C> optional3;
  private final Optional<D> optional4;
  private final Optional<E> optional5;
  private final Optional<F> optional6;

  OptionalStep6(Optional<A> optional1, Optional<B> optional2, Optional<C> optional3, Optional<D> optional4, Optional<E> optional5,
    Optional<F> optional6) {
    this.optional1 = optional1;
    this.optional2 = optional2;
    this.optional3 = optional3;
    this.optional4 = optional4;
    this.optional5 = optional5;
    this.optional6 = optional6;
  }

  public <Z> Optional<Z> yield(Function6<A, B, C, D, E, F, Z> functor) {
    return optional1.flatMap(e1 -> optional2.flatMap(e2 -> optional3.flatMap(e3 -> optional4.flatMap(e4 -> optional5.flatMap(e5 -> optional6
      .map(e6 -> functor.apply(e1, e2, e3, e4, e5, e6)))))));
  }
}
