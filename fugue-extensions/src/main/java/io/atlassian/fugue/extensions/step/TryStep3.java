package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;
import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.extensions.functions.Predicate3;

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

  public <D> TryStep4<A, B, C, D> then(Function3<? super A, ? super B, ? super C, Try<D>> functor) {
    Try<D> try4 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> functor.apply(value1, value2, value3))));
    return new TryStep4<>(try1, try2, try3, try4);
  }

  public <D> TryStep4<A, B, C, D> then(Supplier<Try<D>> supplier) {
    Try<D> try4 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.flatMap(value3 -> supplier.get())));
    return new TryStep4<>(try1, try2, try3, try4);
  }

  public TryStep3<A, B, C> filter(Predicate3<? super A, ? super B, ? super C> predicate, Supplier<Exception> unsatisfiedSupplier) {
    Try<C> filterTry3 = try1.flatMap(value1 -> try2.flatMap(value2 -> try3.filterOrElse(value3 -> predicate.test(value1, value2, value3),
      unsatisfiedSupplier)));
    return new TryStep3<>(try1, try2, filterTry3);
  }

  public <Z> Try<Z> yield(Function3<? super A, ? super B, ? super C, Z> functor) {
    return try1.flatMap(value1 -> try2.flatMap(value2 -> try3.map(value3 -> functor.apply(value1, value2, value3))));
  }

}
