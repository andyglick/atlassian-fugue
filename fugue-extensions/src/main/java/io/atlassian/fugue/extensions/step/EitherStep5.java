package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.extensions.functions.Function5;
import io.atlassian.fugue.extensions.functions.Predicate5;

import java.util.function.Supplier;

public class EitherStep5<A, B, C, D, E, LEFT> {
  private final Either<LEFT, A> either1;
  private final Either<LEFT, B> either2;
  private final Either<LEFT, C> either3;
  private final Either<LEFT, D> either4;
  private final Either<LEFT, E> either5;

  EitherStep5(Either<LEFT, A> either1, Either<LEFT, B> either2, Either<LEFT, C> either3, Either<LEFT, D> either4, Either<LEFT, E> either5) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
    this.either4 = either4;
    this.either5 = either5;
  }

  public <F, LL extends LEFT> EitherStep6<A, B, C, D, E, F, LEFT> then(
    Function5<? super A, ? super B, ? super C, ? super D, ? super E, Either<LL, F>> functor) {
    Either<LEFT, F> either6 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> either5
      .flatMap(value5 -> functor.apply(value1, value2, value3, value4, value5))))));
    return new EitherStep6<>(either1, either2, either3, either4, either5, either6);
  }

  public <F, LL extends LEFT> EitherStep6<A, B, C, D, E, F, LEFT> then(Supplier<Either<LL, F>> supplier) {
    Either<LEFT, F> either6 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> either5
      .flatMap(value5 -> supplier.get())))));
    return new EitherStep6<>(either1, either2, either3, either4, either5, either6);
  }

  public EitherStep5<A, B, C, D, E, LEFT> filter(Predicate5<? super A, ? super B, ? super C, ? super D, ? super E> predicate,
    Supplier<? extends LEFT> unsatisfiedSupplier) {
    Either<LEFT, E> filterEither5 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> either5
      .filterOrElse(value5 -> predicate.test(value1, value2, value3, value4, value5), unsatisfiedSupplier)))));
    return new EitherStep5<>(either1, either2, either3, either4, filterEither5);
  }

  public <Z> Either<LEFT, Z> yield(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Z> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> either4.flatMap(value4 -> either5.map(value5 -> functor
      .apply(value1, value2, value3, value4, value5))))));
  }

}