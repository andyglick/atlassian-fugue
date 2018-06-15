package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.Try;

import java.util.function.Supplier;

public class TryStep3<A, B, C> {

  private final Try<A> try1;
  private final Try<B> try2;
  private final Try<C> try3;

  TryStep3(Try<A> try1, Try<B> try2, Try<C> try3) {
    this.try1 = try1;
    this.try2 = try2;
    this.try3 = try3;
  }

  public <D> TryStep4<A, B, C, D> then(Function3<A, B, C, Try<D>> functor) {
    Try<D> try4 = try1.flatMap(e1 -> try2.flatMap(e2 -> try3.flatMap(e3 -> functor.apply(e1, e2, e3))));
    return new TryStep4<>(try1, try2, try3, try4);
  }

  public <D> TryStep4<A, B, C, D> then(Supplier<Try<D>> supplier) {
    Try<D> try4 = try1.flatMap(e1 -> try2.flatMap(e2 -> try3.flatMap(e3 -> supplier.get())));
    return new TryStep4<>(try1, try2, try3, try4);
  }

  public <Z> Try<Z> yield(Function3<A, B, C, Z> functor) {
    return try1.flatMap(e1 -> try2.flatMap(e2 -> try3.map(e3 -> functor.apply(e1, e2, e3))));
  }

}
