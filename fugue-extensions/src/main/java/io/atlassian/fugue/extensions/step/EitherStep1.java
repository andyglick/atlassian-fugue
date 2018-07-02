package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EitherStep1<A, LEFT> {

  private final Either<LEFT, A> either1;

  EitherStep1(Either<LEFT, A> either1) {
    this.either1 = either1;
  }

  public <B> EitherStep2<A, B, LEFT> then(Function<? super A, Either<LEFT, B>> functor) {
    Either<LEFT, B> either2 = either1.flatMap(functor);
    return new EitherStep2<>(either1, either2);
  }

  public <B> EitherStep2<A, B, LEFT> then(Supplier<Either<LEFT, B>> supplier) {
    Either<LEFT, B> either2 = either1.flatMap(value1 -> supplier.get());
    return new EitherStep2<>(either1, either2);
  }

  public EitherStep1<A, LEFT> filter(Predicate<? super A> predicate,
    Function<Option<LEFT>, ? extends Either<? extends LEFT, ? extends A>> unsatisfiedHandler) {
    Either<LEFT, A> filterEither1 = either1.filter(predicate, unsatisfiedHandler);
    return new EitherStep1<>(filterEither1);
  }

  public <Z> Either<LEFT, Z> yield(Function<? super A, Z> functor) {
    return either1.map(functor);
  }

}
