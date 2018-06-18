package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class OptionStep2<A, B> {

  private final Option<A> option1;
  private final Option<B> option2;

  OptionStep2(Option<A> option1, Option<B> option2) {
    this.option1 = option1;
    this.option2 = option2;
  }

  public <C> OptionStep3<A, B, C> then(BiFunction<A, B, Option<C>> functor) {
    Option<C> option3 = option1.flatMap(value1 -> option2.flatMap(value2 -> functor.apply(value1, value2)));
    return new OptionStep3<>(option1, option2, option3);
  }

  public <C> OptionStep3<A, B, C> then(Supplier<Option<C>> supplier) {
    Option<C> option3 = option1.flatMap(value1 -> option2.flatMap(value2 -> supplier.get()));
    return new OptionStep3<>(option1, option2, option3);
  }

  public OptionStep2<A, B> filter(BiPredicate<A, B> predicate) {
    Option<B> filterOption2 = option1.flatMap(value1 -> option2.filter(value2 -> predicate.test(value1, value2)));
    return new OptionStep2<>(option1, filterOption2);
  }

  public <Z> Option<Z> yield(BiFunction<A, B, Z> functor) {
    return option1.flatMap(value1 -> option2.map(value2 -> functor.apply(value1, value2)));
  }

}
