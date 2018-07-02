package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.Try;
import io.atlassian.fugue.extensions.functions.Function4;
import io.atlassian.fugue.extensions.functions.Predicate4;

import java.util.function.Function;
import java.util.function.Supplier;

public class TryStep4<A, B, C, D> {

  private final Try<A> try1;
  private final Try<B> try2;
  private final Try<C> try3;
  private final Try<D> try4;

  TryStep4(Try<A> try1, Try<B> try2, Try<C> try3, Try<D> try4) {
    this.try1 = try1;
    this.try2 = try2;
    this.try3 = try3;
    this.try4 = try4;
  }

  public <E> TryStep5<A, B, C, D, E> then(Function4<? super A, ? super B, ? super C, ? super D, Try<E>> functor) {
    Try<E> try5 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> functor.apply(value1, value2, value3,
      value4)))));
    return new TryStep5<>(try1, try2, try3, try4, try5);
  }

  public <E> TryStep5<A, B, C, D, E> then(Supplier<Try<E>> supplier) {
    Try<E> try5 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> supplier.get()))));
    return new TryStep5<>(try1, try2, try3, try4, try5);
  }

  public TryStep4<A, B, C, D> filter(Predicate4<? super A, ? super B, ? super C, ? super D> predicate,
    Function<Option<Exception>, Try<D>> unsatisfiedHandler) {
    Try<D> filterTry4 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.filter(
      value4 -> predicate.test(value1, value2, value3, value4), unsatisfiedHandler))));
    return new TryStep4<>(try1, try2, try3, filterTry4);
  }

  public <Z> Try<Z> yield(Function4<? super A, ? super B, ? super C, ? super D, Z> functor) {
    return try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.map(value4 -> functor.apply(value1, value2, value3, value4)))));
  }

}
