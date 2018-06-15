package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function5;
import io.atlassian.fugue.Option;

import java.util.function.Supplier;

public class OptionStep5<A, B, C, D, E> {
  private final Option<A> option1;
  private final Option<B> option2;
  private final Option<C> option3;
  private final Option<D> option4;
  private final Option<E> option5;

  OptionStep5(Option<A> option1, Option<B> option2, Option<C> option3, Option<D> option4, Option<E> option5) {
    this.option1 = option1;
    this.option2 = option2;
    this.option3 = option3;
    this.option4 = option4;
    this.option5 = option5;
  }

  public <F> OptionStep6<A, B, C, D, E, F> then(Function5<A, B, C, D, E, Option<F>> functor) {
    Option<F> option6 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> option5
      .flatMap(value5 -> functor.apply(value1, value2, value3, value4, value5))))));
    return new OptionStep6<>(option1, option2, option3, option4, option5, option6);
  }

  public <F> OptionStep6<A, B, C, D, E, F> then(Supplier<Option<F>> supplier) {
    Option<F> option6 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> option5
      .flatMap(value5 -> supplier.get())))));
    return new OptionStep6<>(option1, option2, option3, option4, option5, option6);
  }

  public <Z> Option<Z> yield(Function5<A, B, C, D, E, Z> functor) {
    return option1.flatMap(e1 -> option2.flatMap(e2 -> option3.flatMap(e3 -> option4.flatMap(e4 -> option5.map(e5 -> functor
      .apply(e1, e2, e3, e4, e5))))));
  }
}
