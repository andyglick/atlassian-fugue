package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.Try;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class TryStep2<A, B> {

  private final Try<A> try1;
  private final Try<B> try2;

  TryStep2(Try<A> try1, Try<B> try2) {
    this.try1 = try1;
    this.try2 = try2;
  }

  public <C> TryStep3<A, B, C> then(BiFunction<? super A, ? super B, Try<C>> functor) {
    Try<C> try3 = try1.flatMap(value1 -> try2.flatMap(value2 -> functor.apply(value1, value2)));
    return new TryStep3<>(try1, try2, try3);
  }

  public <C> TryStep3<A, B, C> then(Supplier<Try<C>> supplier) {
    Try<C> Try = try1.flatMap(value1 -> try2.flatMap(value2 -> supplier.get()));
    return new TryStep3<>(try1, try2, Try);
  }

  public TryStep2<A, B> filter(BiPredicate<? super A, ? super B> predicate, Function<Option<Exception>, Try<B>> unsatisfiedHandler) {
    Try<B> filterTry2 = try1.flatMap(value1 -> try2.filter(value2 -> predicate.test(value1, value2), unsatisfiedHandler));
    return new TryStep2<>(try1, filterTry2);
  }

  public <Z> Try<Z> yield(BiFunction<? super A, ? super B, Z> functor) {
    return try1.flatMap(value1 -> try2.map(value2 -> functor.apply(value1, value2)));
  }

}
