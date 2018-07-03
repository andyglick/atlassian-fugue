package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.extensions.functions.Function4;
import io.atlassian.fugue.extensions.functions.Predicate4;

import java.util.function.Function;
import java.util.function.Supplier;

public class EitherStep4<A, B, C, D, LEFT> {
  private final Either<LEFT, A> either1;
  private final Either<LEFT, B> either2;
  private final Either<LEFT, C> either3;
  private final Either<LEFT, D> either4;

  EitherStep4(Either<LEFT, A> either1, Either<LEFT, B> either2, Either<LEFT, C> either3, Either<LEFT, D> either4) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
    this.either4 = either4;
  }

  public <E, LL extends LEFT> EitherStep5<A, B, C, D, E, LEFT> then(Function4<? super A, ? super B, ? super C, ? super D, Either<LL, E>> functor) {
    Either<LEFT, E> either5 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> functor.apply(
      value1, value2, value3, value4)))));
    return new EitherStep5<>(either1, either2, either3, either4, either5);
  }

  public <E, LL extends LEFT> EitherStep5<A, B, C, D, E, LEFT> then(Supplier<Either<LL, E>> supplier) {
    Either<LEFT, E> either5 = either1
      .flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> supplier.get()))));
    return new EitherStep5<>(either1, either2, either3, either4, either5);
  }

  public EitherStep4<A, B, C, D, LEFT> filter(Predicate4<? super A, ? super B, ? super C, ? super D> predicate,
    Function<Option<LEFT>, ? extends Either<? extends LEFT, ? extends D>> unsatisfiedHandler) {
    Either<LEFT, D> filterEither4 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.filter(
      value4 -> predicate.test(value1, value2, value3, value4), unsatisfiedHandler))));
    return new EitherStep4<>(either1, either2, either3, filterEither4);
  }

  public <Z> Either<LEFT, Z> yield(Function4<? super A, ? super B, ? super C, ? super D, Z> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.map(value4 -> functor.apply(value1, value2, value3,
      value4)))));
  }

}
