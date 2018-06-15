package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function6;
import io.atlassian.fugue.Try;

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

  public <Z> Try<Z> yield(Function6<A, B, C, D, E, F, Z> functor) {
    return try1.flatMap(e1 -> try2.flatMap(e2 -> try3.flatMap(e3 -> try4.flatMap(e4 -> try5.flatMap(e5 -> try6.map(e6 -> functor.apply(e1, e2, e3,
      e4, e5, e6)))))));
  }
}
