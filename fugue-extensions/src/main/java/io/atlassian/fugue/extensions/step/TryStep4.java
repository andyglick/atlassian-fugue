package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function4;
import io.atlassian.fugue.Try;

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

  public <E> TryStep5<A, B, C, D, E> then(Function4<A, B, C, D, Try<E>> functor) {
    Try<E> try5 = try1.flatMap(e1 -> try2.flatMap(e2 -> try3.flatMap(e3 -> try4.flatMap(e4 -> functor.apply(e1, e2, e3, e4)))));
    return new TryStep5<>(try1, try2, try3, try4, try5);
  }

  public <E> TryStep5<A, B, C, D, E> then(Supplier<Try<E>> supplier) {
    Try<E> try5 = try1.flatMap(e1 -> try2.flatMap(e2 -> try3.flatMap(e3 -> try4.flatMap(e4 -> supplier.get()))));
    return new TryStep5<>(try1, try2, try3, try4, try5);
  }

  public <Z> Try<Z> yield(Function4<A, B, C, D, Z> functor) {
    return try1.flatMap(e1 -> try2.flatMap(e2 -> try3.flatMap(e3 -> try4.map(e4 -> functor.apply(e1, e2, e3, e4)))));
  }

}
