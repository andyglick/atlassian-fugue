package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function4;
import io.atlassian.fugue.Either;

import java.util.function.Supplier;

public class EitherStep4<E1, E2, E3, E4, E> {
  private final Either<E, E1> either1;
  private final Either<E, E2> either2;
  private final Either<E, E3> either3;
  private final Either<E, E4> either4;

  EitherStep4(Either<E, E1> either1, Either<E, E2> either2, Either<E, E3> either3, Either<E, E4> either4) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
    this.either4 = either4;
  }

  public <E5> EitherStep5<E1, E2, E3, E4, E5, E> then(Function4<E1, E2, E3, E4, Either<E, E5>> functor) {
    Either<E, E5> either5 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> functor.apply(
      value1, value2, value3, value4)))));
    return new EitherStep5<>(either1, either2, either3, either4, either5);
  }

  public <E5> EitherStep5<E1, E2, E3, E4, E5, E> then(Supplier<Either<E, E5>> supplier) {
    Either<E, E5> either5 = either1
      .flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> supplier.get()))));
    return new EitherStep5<>(either1, either2, either3, either4, either5);
  }

  public <RESULT> Either<E, RESULT> yield(Function4<E1, E2, E3, E4, RESULT> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.map(value4 -> functor.apply(value1, value2, value3,
      value4)))));
  }

}
