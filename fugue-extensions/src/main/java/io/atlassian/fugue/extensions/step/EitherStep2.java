package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class EitherStep2<E1, E2, E> {

  private final Either<E, E1> either1;
  private final Either<E, E2> either2;

  EitherStep2(Either<E, E1> either1, Either<E, E2> either2) {
    this.either1 = either1;
    this.either2 = either2;
  }

  public <E3> EitherStep3<E1, E2, E3, E> then(BiFunction<E1, E2, Either<E, E3>> functor) {
    Either<E, E3> either3 = either1.flatMap(value1 -> either2.flatMap(value2 -> functor.apply(value1, value2)));
    return new EitherStep3<>(either1, either2, either3);
  }

  public <E3> EitherStep3<E1, E2, E3, E> then(Supplier<Either<E, E3>> supplier) {
    Either<E, E3> either3 = either1.flatMap(value1 -> either2.flatMap(value2 -> supplier.get()));
    return new EitherStep3<>(either1, either2, either3);
  }

  public <Z> Either<E, Z> yield(BiFunction<E1, E2, Z> functor) {
    return either1.flatMap(value1 -> either2.map(value2 -> functor.apply(value1, value2)));
  }

}
