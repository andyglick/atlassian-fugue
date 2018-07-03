package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.extensions.functions.Function3;
import io.atlassian.fugue.extensions.functions.Predicate3;

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

  public <D> OptionStep4<A, B, C, D> then(Function3<? super A, ? super B, ? super C, ? extends Option<? extends D>> functor) {
    Option<D> option4 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> functor.apply(value1, value2, value3))));
    return new OptionStep4<>(option1, option2, option3, option4);
  }

  public <D> OptionStep4<A, B, C, D> then(Supplier<? extends Option<? extends D>> supplier) {
    Option<D> option4 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.flatMap(value3 -> supplier.get())));
    return new OptionStep4<>(option1, option2, option3, option4);
  }

  public OptionStep3<A, B, C> filter(Predicate3<? super A, ? super B, ? super C> predicate) {
    Option<C> filterOption3 = option1.flatMap(value1 -> option2.flatMap(value2 -> option3.filter(value3 -> predicate.test(value1, value2, value3))));
    return new OptionStep3<>(option1, option2, filterOption3);
  }

  public <Z> Option<Z> yield(Function3<? super A, ? super B, ? super C, Z> functor) {
    return option1.flatMap(value1 -> option2.flatMap(value2 -> option3.map(value3 -> functor.apply(value1, value2, value3))));
  }

}
