package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.Try;
import io.atlassian.fugue.extensions.functions.Function5;
import io.atlassian.fugue.extensions.functions.Predicate5;

import java.util.function.Function;
import java.util.function.Supplier;

public class TryStep5<A, B, C, D, E> {
  private final Try<A> try1;
  private final Try<B> try2;
  private final Try<C> try3;
  private final Try<D> try4;
  private final Try<E> try5;

  TryStep5(Try<A> try1, Try<B> try2, Try<C> try3, Try<D> try4, Try<E> try5) {
    this.try1 = try1;
    this.try2 = try2;
    this.try3 = try3;
    this.try4 = try4;
    this.try5 = try5;
  }

  public <F> TryStep6<A, B, C, D, E, F> then(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Try<F>> functor) {
    Try<F> try6 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.flatMap(value5 -> functor.apply(
      value1, value2, value3, value4, value5))))));
    return new TryStep6<>(try1, try2, try3, try4, try5, try6);
  }

  public <F> TryStep6<A, B, C, D, E, F> then(Supplier<Try<F>> supplier) {
    Try<F> try6 = try1
      .flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.flatMap(value5 -> supplier.get())))));
    return new TryStep6<>(try1, try2, try3, try4, try5, try6);
  }

  public TryStep5<A, B, C, D, E> filter(Predicate5<? super A, ? super B, ? super C, ? super D, ? super E> predicate,
    Function<Option<Exception>, Try<E>> unsatisfiedHandler) {
    Try<E> filterTry5 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.filter(
      value5 -> predicate.test(value1, value2, value3, value4, value5), unsatisfiedHandler)))));
    return new TryStep5<>(try1, try2, try3, try4, filterTry5);
  }

  public <Z> Try<Z> yield(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Z> functor) {
    return try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.map(value5 -> functor.apply(value1,
      value2, value3, value4, value5))))));
  }
}
