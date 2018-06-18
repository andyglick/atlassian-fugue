package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.extensions.functions.Predicate3;

import java.util.function.Supplier;

import static io.atlassian.fugue.Either.left;

public class EitherStep3<A, B, C, LEFT> {

  private final Either<LEFT, A> either1;
  private final Either<LEFT, B> either2;
  private final Either<LEFT, C> either3;

  EitherStep3(Either<LEFT, A> either1, Either<LEFT, B> either2, Either<LEFT, C> either3) {
    this.either1 = either1;
    this.either2 = either2;
    this.either3 = either3;
  }

  public <D> EitherStep4<A, B, C, D, LEFT> then(Function3<A, B, C, Either<LEFT, D>> functor) {
    Either<LEFT, D> either4 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> functor.apply(value1, value2, value3))));
    return new EitherStep4<>(either1, either2, either3, either4);
  }

  public <D> EitherStep4<A, B, C, D, LEFT> then(Supplier<Either<LEFT, D>> supplier) {
    Either<LEFT, D> either4 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.flatMap(value3 -> supplier.get())));
    return new EitherStep4<>(either1, either2, either3, either4);
  }

  public EitherStep3<A, B, C, LEFT> filter(Predicate3<A, B, C> predicate, Supplier<LEFT> leftSupplier) {
    Either<LEFT, C> filterEither3 = either1.flatMap(value1 -> either2.flatMap(value2 -> either3.filter(
      value3 -> predicate.test(value1, value2, value3)).getOr(() -> left(leftSupplier.get()))));
    return new EitherStep3<>(either1, either2, filterEither3);
  }

  public <Z> Either<LEFT, Z> yield(Function3<A, B, C, Z> functor) {
    return either1.flatMap(value1 -> either2.flatMap(value2 -> either3.map(value3 -> functor.apply(value1, value2, value3))));
  }

}
