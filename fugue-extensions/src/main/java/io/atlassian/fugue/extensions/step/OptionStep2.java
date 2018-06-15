package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OptionStep2<A, B> {

  private final Option<A> option1;
  private final Option<B> option2;

  OptionStep2(Option<A> option1, Option<B> option2) {
    this.option1 = option1;
    this.option2 = option2;
  }

  public <C> OptionStep3<A, B, C> then(BiFunction<A, B, Option<C>> functor) {
    Option<C> option3 = option1.flatMap(e1 -> option2.flatMap(e2 -> functor.apply(e1, e2)));
    return new OptionStep3<>(option1, option2, option3);
  }

  public <C> OptionStep3<A, B, C> then(Supplier<Option<C>> supplier) {
    Option<C> option3 = option1.flatMap(e1 -> option2.flatMap(e2 -> supplier.get()));
    return new OptionStep3<>(option1, option2, option3);
  }

  public <Z> Option<Z> yield(BiFunction<A, B, Z> functor) {
    return option1.flatMap(e1 -> option2.map(e2 -> functor.apply(e1, e2)));
  }

}
