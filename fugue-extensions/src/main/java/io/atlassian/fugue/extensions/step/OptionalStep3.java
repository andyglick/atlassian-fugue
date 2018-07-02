package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.extensions.functions.Predicate3;

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

  public <D> OptionalStep4<A, B, C, D> then(Function3<? super A, ? super B, ? super C, Optional<D>> functor) {
    Optional<D> option4 = optional1
      .flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> functor.apply(value1, value2, value3))));
    return new OptionalStep4<>(optional1, optional2, optional3, option4);
  }

  public <D> OptionalStep4<A, B, C, D> then(Supplier<Optional<D>> supplier) {
    Optional<D> Optional = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> supplier.get())));
    return new OptionalStep4<>(optional1, optional2, optional3, Optional);
  }

  public OptionalStep3<A, B, C> filter(Predicate3<? super A, ? super B, ? super C> predicate) {
    Optional<C> filterOptional3 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.filter(value3 -> predicate.test(value1, value2,
      value3))));
    return new OptionalStep3<>(optional1, optional2, filterOptional3);
  }

  public <Z> Optional<Z> yield(Function3<? super A, ? super B, ? super C, Z> functor) {
    return optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.map(value3 -> functor.apply(value1, value2, value3))));
  }

}
