package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.Either;

import java.util.function.Supplier;

public class EitherStep3<E1, E2, E3, E> {

  private final Either<E, E1> either1;
  private final Either<E, E2> either2;
  private final Either<E, E3> either3;

  EitherStep3(Either<E, E1> either1, Either<E, E2> either2, Either<E, E3> either3) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
  }

  public <E4> EitherStep4<E1, E2, E3, E4, E> then(Function3<E1, E2, E3, Either<E, E4>> functor) {
    Either<E, E4> either4 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> functor.apply(value1, value2, value3))));
    return new EitherStep4<>(either1, either2, either3, either4);
  }

  public <E4> EitherStep4<E1, E2, E3, E4, E> then(Supplier<Either<E, E4>> supplier) {
    Either<E, E4> either4 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> supplier.get())));
    return new EitherStep4<>(either1, either2, either3, either4);
  }

  public <Z> Either<E, Z> yield(Function3<E1, E2, E3, Z> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.map(value3 -> functor.apply(value1, value2, value3))));
  }

}
