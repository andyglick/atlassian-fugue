package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function4;
import io.atlassian.fugue.extensions.functions.Predicate4;

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

  public <E> OptionalStep5<A, B, C, D, E> then(Function4<? super A, ? super B, ? super C, ? super D, Optional<E>> functor) {
    Optional<E> option5 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.flatMap(value4 -> functor
      .apply(value1, value2, value3, value4)))));

    return new OptionalStep5<>(optional1, optional2, optional3, optional4, option5);
  }

  public <E> OptionalStep5<A, B, C, D, E> then(Supplier<Optional<E>> supplier) {
    Optional<E> Optional = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.flatMap(value4 -> supplier
      .get()))));
    return new OptionalStep5<>(optional1, optional2, optional3, optional4, Optional);
  }

  public OptionalStep4<A, B, C, D> filter(Predicate4<? super A, ? super B, ? super C, ? super D> predicate) {
    Optional<D> filterOptional4 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4
      .filter(value4 -> predicate.test(value1, value2, value3, value4)))));
    return new OptionalStep4<>(optional1, optional2, optional3, filterOptional4);
  }

  public <Z> Optional<Z> yield(Function4<? super A, ? super B, ? super C, ? super D, Z> functor) {
    return optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.map(value4 -> functor.apply(value1, value2,
      value3, value4)))));
  }

}
