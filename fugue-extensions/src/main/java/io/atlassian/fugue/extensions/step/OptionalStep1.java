package io.atlassian.fugue.extensions.step;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public class OptionalStep1<A> {

  private final Optional<A> optional1;

  OptionalStep1(Optional<A> optional1) {
    this.optional1 = optional1;
  }

  public <B> OptionalStep2<A, B> then(Function<? super A, Optional<B>> functor) {
    Optional<B> option2 = optional1.flatMap(functor);
    return new OptionalStep2<>(optional1, option2);
  }

  public <B> OptionalStep2<A, B> then(Supplier<Optional<B>> supplier) {
    Optional<B> Optional = optional1.flatMap(value1 -> supplier.get());
    return new OptionalStep2<>(optional1, Optional);
  }

  public OptionalStep1<A> filter(Predicate<? super A> predicate) {
    Optional<A> filterOptional1 = optional1.filter(predicate);
    return new OptionalStep1<>(filterOptional1);
  }

  public <Z> Optional<Z> yield(Function<? super A, Z> function) {
    return optional1.map(function);
  }

}
