package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function6;
import io.atlassian.fugue.Either;

public class EitherStep6<E1, E2, E3, E4, E5, E6, E> {
  private final Either<E, E1> either1;
  private final Either<E, E2> either2;
  private final Either<E, E3> either3;
  private final Either<E, E4> either4;
  private final Either<E, E5> either5;
  private final Either<E, E6> either6;

  EitherStep6(Either<E, E1> either1, Either<E, E2> either2, Either<E, E3> either3, Either<E, E4> either4, Either<E, E5> either5, Either<E, E6> either6) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
    this.either4 = either4;
    this.either5 = either5;
    this.either6 = either6;
  }

  public <Z> Either<E, Z> yield(Function6<E1, E2, E3, E4, E5, E6, Z> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> either5.flatMap(value5 -> either6
      .map(value6 -> functor.apply(value1, value2, value3, value4, value5, value6)))))));
  }

}