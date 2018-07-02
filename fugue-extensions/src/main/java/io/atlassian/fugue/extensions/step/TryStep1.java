package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.Try;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TryStep1<A> {

  private final Try<A> try1;

  TryStep1(Try<A> try1) {
    this.try1 = try1;
  }

  public <B> TryStep2<A, B> then(Function<? super A, Try<B>> functor) {
    Try<B> try2 = try1.flatMap(functor);
    return new TryStep2<>(try1, try2);
  }

  public <B> TryStep2<A, B> then(Supplier<Try<B>> supplier) {
    Try<B> either2 = try1.flatMap(value1 -> supplier.get());
    return new TryStep2<>(try1, either2);
  }

  public TryStep1<A> filter(Predicate<? super A> predicate, Function<Option<Exception>, Try<A>> unsatisfiedHandler) {
    Try<A> filterTry1 = try1.filter(predicate, unsatisfiedHandler);
    return new TryStep1<>(filterTry1);
  }

  public <Z> Try<Z> yield(Function<? super A, Z> function) {
    return try1.map(function);
  }

}
