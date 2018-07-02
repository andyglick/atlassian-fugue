package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function5;
import io.atlassian.fugue.extensions.functions.Predicate5;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public class OptionalStep5<A, B, C, D, E> {
  private final Optional<A> optional1;
  private final Optional<B> optional2;
  private final Optional<C> optional3;
  private final Optional<D> optional4;
  private final Optional<E> optional5;

  OptionalStep5(Optional<A> optional1, Optional<B> optional2, Optional<C> optional3, Optional<D> optional4, Optional<E> optional5) {
    this.optional1 = optional1;
    this.optional2 = optional2;
    this.optional3 = optional3;
    this.optional4 = optional4;
    this.optional5 = optional5;
  }

  public <F> OptionalStep6<A, B, C, D, E, F> then(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Optional<F>> functor) {
    Optional<F> optional6 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.flatMap(value4 -> optional5
      .flatMap(value5 -> functor.apply(value1, value2, value3, value4, value5))))));
    return new OptionalStep6<>(optional1, optional2, optional3, optional4, optional5, optional6);
  }

  public <F> OptionalStep6<A, B, C, D, E, F> then(Supplier<Optional<F>> supplier) {
    Optional<F> optional6 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.flatMap(value4 -> optional5
      .flatMap(value5 -> supplier.get())))));
    return new OptionalStep6<>(optional1, optional2, optional3, optional4, optional5, optional6);
  }

  public OptionalStep5<A, B, C, D, E> filter(Predicate5<? super A, ? super B, ? super C, ? super D, ? super E> predicate) {
    Optional<E> filterOptional5 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4
      .flatMap(value4 -> optional5.filter(value5 -> predicate.test(value1, value2, value3, value4, value5))))));
    return new OptionalStep5<>(optional1, optional2, optional3, optional4, filterOptional5);
  }

  public <Z> Optional<Z> yield(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Z> functor) {
    return optional1.flatMap(value1 -> optional2.flatMap(value2 -> optional3.flatMap(value3 -> optional4.flatMap(value4 -> optional5
      .map(value5 -> functor.apply(value1, value2, value3, value4, value5))))));
  }
}
