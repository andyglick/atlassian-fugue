package io.atlassian.fugue.extensions.step;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public class OptionalStep2<A, B> {

  private final Optional<A> optional1;
  private final Optional<B> optional2;

  OptionalStep2(Optional<A> optional1, Optional<B> optional2) {
    this.optional1 = optional1;
    this.optional2 = optional2;
  }

  public <C> OptionalStep3<A, B, C> then(BiFunction<? super A, ? super B, Optional<C>> functor) {
    Optional<C> option3 = optional1.flatMap(value1 -> optional2.flatMap(value2 -> functor.apply(value1, value2)));
    return new OptionalStep3<>(optional1, optional2, option3);
  }

  public <C> OptionalStep3<A, B, C> then(Supplier<Optional<C>> supplier) {
    Optional<C> Optional = optional1.flatMap(value1 -> optional2.flatMap(value2 -> supplier.get()));
    return new OptionalStep3<>(optional1, optional2, Optional);
  }

  public OptionalStep2<A, B> filter(BiPredicate<? super A, ? super B> predicate) {
    Optional<B> filterOptional2 = optional1.flatMap(value1 -> optional2.filter(value2 -> predicate.test(value1, value2)));
    return new OptionalStep2<>(optional1, filterOptional2);
  }

  public <Z> Optional<Z> yield(BiFunction<? super A, ? super B, Z> functor) {
    return optional1.flatMap(value1 -> optional2.map(value2 -> functor.apply(value1, value2)));
  }

}
