package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.extensions.functions.Function6;
import io.atlassian.fugue.extensions.functions.Predicate6;

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

  public OptionStep6<A, B, C, D, E, F> filter(Predicate6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> predicate) {
    Option<F> filterOption6 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> option5
      .flatMap(value5 -> option6.filter(value6 -> predicate.test(value1, value2, value3, value4, value5, value6)))))));
    return new OptionStep6<>(option1, option2, option3, option4, option5, filterOption6);
  }

  public <Z> Option<Z> yield(Function6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, Z> functor) {
    return option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> option5.flatMap(value5 -> option6
      .map(value6 -> functor.apply(value1, value2, value3, value4, value5, value6)))))));
  }
}
