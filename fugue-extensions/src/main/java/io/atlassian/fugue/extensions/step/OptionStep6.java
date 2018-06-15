package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.extensions.functions.Function6;
import io.atlassian.fugue.Option;

public class OptionStep6<A, B, C, D, E, F> {
  private final Option<A> option1;
  private final Option<B> option2;
  private final Option<C> option3;
  private final Option<D> option4;
  private final Option<E> option5;
  private final Option<F> option6;

  OptionStep6(Option<A> option1, Option<B> option2, Option<C> option3, Option<D> option4, Option<E> option5, Option<F> option6) {
    this.option1 = option1;
    this.option2 = option2;
    this.option3 = option3;
    this.option4 = option4;
    this.option5 = option5;
    this.option6 = option6;
  }

  public <Z> Option<Z> yield(Function6<A, B, C, D, E, F, Z> functor) {
    return option1.flatMap(e1 -> option2.flatMap(e2 -> option3.flatMap(e3 -> option4.flatMap(e4 -> option5.flatMap(e5 -> option6.map(e6 -> functor
      .apply(e1, e2, e3, e4, e5, e6)))))));
  }
}
