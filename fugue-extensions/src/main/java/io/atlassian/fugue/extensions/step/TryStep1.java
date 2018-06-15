package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;

import java.util.function.Function;
import java.util.function.Supplier;

public class TryStep1<E1> {

  private final Try<E1> try1;

  TryStep1(Try<E1> try1) {
    this.try1 = try1;
  }

  public <E2> TryStep2<E1, E2> then(Function<E1, Try<E2>> functor) {
    Try<E2> try2 = try1.flatMap(functor);
    return new TryStep2<>(try1, try2);
  }

  public <E2> TryStep2<E1, E2> then(Supplier<Try<E2>> supplier) {
    Try<E2> either2 = try1.flatMap(e1 -> supplier.get());
    return new TryStep2<>(try1, either2);
  }

  public <Z> Try<Z> yield(Function<E1, Z> function) {
    return try1.map(function);
  }

}
