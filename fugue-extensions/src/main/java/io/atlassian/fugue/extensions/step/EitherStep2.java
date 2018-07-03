package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class EitherStep2<A, B, LEFT> {

  private final Either<LEFT, A> either1;
  private final Either<LEFT, B> either2;

  EitherStep2(Either<LEFT, A> either1, Either<LEFT, B> either2) {
    this.either1 = either1;
    this.either2 = either2;
  }

  public <C, LL extends LEFT> EitherStep3<A, B, C, LEFT> then(BiFunction<? super A, ? super B, Either<LL, C>> functor) {
    Either<LEFT, C> either3 = either1.flatMap(value1 -> either2.flatMap(value2 -> functor.apply(value1, value2)));
    return new EitherStep3<>(either1, either2, either3);
  }

  public <C, LL extends LEFT> EitherStep3<A, B, C, LEFT> then(Supplier<Either<LL, C>> supplier) {
    Either<LEFT, C> either3 = either1.flatMap(value1 -> either2.flatMap(value2 -> supplier.get()));
    return new EitherStep3<>(either1, either2, either3);
  }

  public EitherStep2<A, B, LEFT> filter(BiPredicate<? super A, ? super B> predicate,
    Function<Option<LEFT>, ? extends Either<? extends LEFT, ? extends B>> unsatisfiedHandler) {
    Either<LEFT, B> filterEither2 = either1.flatMap(value1 -> either2.filter(value2 -> predicate.test(value1, value2), unsatisfiedHandler));
    return new EitherStep2<>(either1, filterEither2);
  }

  public <Z> Either<LEFT, Z> yield(BiFunction<? super A, ? super B, Z> functor) {
    return either1.flatMap(value1 -> either2.map(value2 -> functor.apply(value1, value2)));
  }

}
