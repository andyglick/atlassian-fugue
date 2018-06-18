package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.extensions.functions.Function4;
import io.atlassian.fugue.extensions.functions.Predicate4;

import java.util.function.Supplier;

public class OptionStep4<A, B, C, D> {

  private final Option<A> option1;
  private final Option<B> option2;
  private final Option<C> option3;
  private final Option<D> option4;

  OptionStep4(Option<A> option1, Option<B> option2, Option<C> option3, Option<D> option4) {
    this.option1 = option1;
    this.option2 = option2;
    this.option3 = option3;
    this.option4 = option4;
  }

  public <E> OptionStep5<A, B, C, D, E> then(Function4<A, B, C, D, Option<E>> functor) {
    Option<E> option5 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> functor.apply(value1,
      value2, value3, value4)))));
    return new OptionStep5<>(option1, option2, option3, option4, option5);
  }

  public <E> OptionStep5<A, B, C, D, E> then(Supplier<Option<E>> supplier) {
    Option<E> option5 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.flatMap(value4 -> supplier.get()))));
    return new OptionStep5<>(option1, option2, option3, option4, option5);
  }

  public OptionStep4<A, B, C, D> filter(Predicate4<A, B, C, D> predicate) {
    Option<D> filterOption4 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.filter(value4 -> predicate.test(
      value1, value2, value3, value4)))));
    return new OptionStep4<>(option1, option2, option3, filterOption4);
  }

  public <Z> Option<Z> yield(Function4<A, B, C, D, Z> functor) {
    return option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> option4.map(value4 -> functor.apply(value1, value2, value3,
      value4)))));
  }

}
