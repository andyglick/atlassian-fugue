package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function4;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public class OptionalStep4<A, B, C, D> {

  private final Optional<A> optional1;
  private final Optional<B> optional2;
  private final Optional<C> optional3;
  private final Optional<D> optional4;

  OptionalStep4(Optional<A> optional1, Optional<B> optional2, Optional<C> optional3, Optional<D> optional4) {
    this.optional1 = optional1;
    this.optional2 = optional2;
    this.optional3 = optional3;
    this.optional4 = optional4;
  }

  public <E> OptionalStep5<A, B, C, D, E> then(Function4<A, B, C, D, Optional<E>> functor) {
    Optional<E> option5 = optional1.flatMap(e1 -> optional2.flatMap(e2 -> optional3.flatMap(e3 -> optional4.flatMap(e4 -> functor.apply(e1, e2, e3,
      e4)))));

    return new OptionalStep5<>(optional1, optional2, optional3, optional4, option5);
  }

  public <E> OptionalStep5<A, B, C, D, E> then(Supplier<Optional<E>> supplier) {
    Optional<E> Optional = optional1.flatMap(e1 -> optional2.flatMap(e2 -> optional3.flatMap(e3 -> optional4.flatMap(e4 -> supplier.get()))));
    return new OptionalStep5<>(optional1, optional2, optional3, optional4, Optional);
  }

  public <Z> Optional<Z> yield(Function4<A, B, C, D, Z> functor) {
    return optional1.flatMap(e1 -> optional2.flatMap(e2 -> optional3.flatMap(e3 -> optional4.map(e4 -> functor.apply(e1, e2, e3, e4)))));
  }

}
