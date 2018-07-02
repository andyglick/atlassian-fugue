package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.Try;
import io.atlassian.fugue.extensions.functions.Function6;
import io.atlassian.fugue.extensions.functions.Predicate6;

import java.util.function.Function;

public class TryStep6<A, B, C, D, E, F> {
  private final Try<A> try1;
  private final Try<B> try2;
  private final Try<C> try3;
  private final Try<D> try4;
  private final Try<E> try5;
  private final Try<F> try6;

  TryStep6(Try<A> try1, Try<B> try2, Try<C> try3, Try<D> try4, Try<E> try5, Try<F> try6) {
    this.try1 = try1;
    this.try2 = try2;
    this.try3 = try3;
    this.try4 = try4;
    this.try5 = try5;
    this.try6 = try6;
  }

  public TryStep6<A, B, C, D, E, F> filter(Predicate6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> predicate,
    Function<Option<Exception>, Try<F>> unsatisfiedHandler) {
    Try<F> filterTry6 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.flatMap(value5 -> try6
      .filter(value6 -> predicate.test(value1, value2, value3, value4, value5, value6), unsatisfiedHandler))))));
    return new TryStep6<>(try1, try2, try3, try4, try5, filterTry6);
  }

  public <Z> Try<Z> yield(Function6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, Z> functor) {
    return try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> try4.flatMap(value4 -> try5.flatMap(value5 -> try6
      .map(value6 -> functor.apply(value1, value2, value3, value4, value5, value6)))))));
  }
}
