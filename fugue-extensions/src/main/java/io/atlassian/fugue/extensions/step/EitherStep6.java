package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.extensions.functions.Function6;
import io.atlassian.fugue.extensions.functions.Predicate6;

import java.util.function.Function;

public class EitherStep6<A, B, C, D, E, F, LEFT> {
  private final Either<LEFT, A> either1;
  private final Either<LEFT, B> either2;
  private final Either<LEFT, C> either3;
  private final Either<LEFT, D> either4;
  private final Either<LEFT, E> either5;
  private final Either<LEFT, F> either6;

  EitherStep6(Either<LEFT, A> either1, Either<LEFT, B> either2, Either<LEFT, C> either3, Either<LEFT, D> either4, Either<LEFT, E> either5,
    Either<LEFT, F> either6) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
    this.either4 = either4;
    this.either5 = either5;
    this.either6 = either6;
  }

  public EitherStep6<A, B, C, D, E, F, LEFT> filter(Predicate6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> predicate,
    Function<Option<LEFT>, ? extends Either<? extends LEFT, ? extends F>> unsatisfiedHandler) {
    Either<LEFT, F> filterEither6 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> either5
      .flatMap(value5 -> either6.filter(value6 -> predicate.test(value1, value2, value3, value4, value5, value6), unsatisfiedHandler))))));
    return new EitherStep6<>(either1, either2, either3, either4, either5, filterEither6);
  }

  public <Z> Either<LEFT, Z> yield(Function6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, Z> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> either5.flatMap(value5 -> either6
      .map(value6 -> functor.apply(value1, value2, value3, value4, value5, value6)))))));
  }

}