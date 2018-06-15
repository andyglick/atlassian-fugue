package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;

import java.util.function.Function;
import java.util.function.Supplier;

public class EitherStep1<E1, E> {

  private final Either<E, E1> either1;

  EitherStep1(Either<E, E1> either1) {
    this.either1 = either1;
  }

  public <E2> EitherStep2<E1, E2, E> then(Function<E1, Either<E, E2>> functor) {
    Either<E, E2> either2 = either1.flatMap(functor);
    return new EitherStep2<>(either1, either2);
  }

  public <E2> EitherStep2<E1, E2, E> then(Supplier<Either<E, E2>> supplier) {
    Either<E, E2> either2 = either1.flatMap(e1 -> supplier.get());
    return new EitherStep2<>(either1, either2);
  }

  public <Z> Either<E, Z> yield(Function<E1, Z> functor) {
    return either1.map(functor);
  }

}
