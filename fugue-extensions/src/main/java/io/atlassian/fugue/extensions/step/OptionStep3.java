package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.Option;

import java.util.function.Supplier;

public class OptionStep3<A, B, C> {

  private final Option<A> option1;
  private final Option<B> option2;
  private final Option<C> option3;

  OptionStep3(Option<A> option1, Option<B> option2, Option<C> option3) {
    this.option1 = option1;
    this.option2 = option2;
    this.option3 = option3;
  }

  public <D> OptionStep4<A, B, C, D> then(Function3<A, B, C, Option<D>> functor) {
    Option<D> option4 = option1.flatMap(e1 -> option2.flatMap(e2 -> option3.flatMap(e3 -> functor.apply(e1, e2, e3))));
    return new OptionStep4<>(option1, option2, option3, option4);
  }

  public <D> OptionStep4<A, B, C, D> then(Supplier<Option<D>> supplier) {
    Option<D> option4 = option1.flatMap(e1 -> option2.flatMap(e2 -> option3.flatMap(e3 -> supplier.get())));
    return new OptionStep4<>(option1, option2, option3, option4);
  }

  public <Z> Option<Z> yield(Function3<A, B, C, Z> functor) {
    return option1.flatMap(e1 -> option2.flatMap(e2 -> option3.map(e3 -> functor.apply(e1, e2, e3))));
  }

}
