package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function3;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public class OptionalStep3<A, B, C> {

  private final Optional<A> optional1;
  private final Optional<B> optional2;
  private final Optional<C> optional3;

  OptionalStep3(Optional<A> optional1, Optional<B> optional2, Optional<C> optional3) {
    this.optional1 = optional1;
    this.optional2 = optional2;
    this.optional3 = optional3;
  }

  public <D> OptionalStep4<A, B, C, D> then(Function3<A, B, C, Optional<D>> functor) {
    Optional<D> option4 = optional1.flatMap(e1 -> optional2.flatMap(e2 -> optional3.flatMap(e3 -> functor.apply(e1, e2, e3))));
    return new OptionalStep4<>(optional1, optional2, optional3, option4);
  }

  public <D> OptionalStep4<A, B, C, D> then(Supplier<Optional<D>> supplier) {
    Optional<D> Optional = optional1.flatMap(e1 -> optional2.flatMap(e2 -> optional3.flatMap(e3 -> supplier.get())));
    return new OptionalStep4<>(optional1, optional2, optional3, Optional);
  }

  public <Z> Optional<Z> yield(Function3<A, B, C, Z> functor) {
    return optional1.flatMap(e1 -> optional2.flatMap(e2 -> optional3.map(e3 -> functor.apply(e1, e2, e3))));
  }

}
