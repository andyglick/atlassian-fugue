package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.extensions.functions.Function5;
import io.atlassian.fugue.extensions.functions.Predicate5;

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

  public <F> OptionStep6<A, B, C, D, E, F> then(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Option<F>> functor) {
    Option<F> option6 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> option5
      .flatMap(value5 -> functor.apply(value1, value2, value3, value4, value5))))));
    return new OptionStep6<>(option1, option2, option3, option4, option5, option6);
  }

  public <F> OptionStep6<A, B, C, D, E, F> then(Supplier<Option<F>> supplier) {
    Option<F> option6 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> option5
      .flatMap(value5 -> supplier.get())))));
    return new OptionStep6<>(option1, option2, option3, option4, option5, option6);
  }

  public OptionStep5<A, B, C, D, E> filter(Predicate5<? super A, ? super B, ? super C, ? super D, ? super E> predicate) {
    Option<E> filterOption5 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> option5
      .filter(value5 -> predicate.test(value1, value2, value3, value4, value5))))));
    return new OptionStep5<>(option1, option2, option3, option4, filterOption5);
  }

  public <Z> Option<Z> yield(Function5<? super A, ? super B, ? super C, ? super D, ? super E, Z> functor) {
    return option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> option5.map(value5 -> functor
      .apply(value1, value2, value3, value4, value5))))));
  }
}
