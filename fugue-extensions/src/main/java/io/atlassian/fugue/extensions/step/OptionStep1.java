package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionStep1<A> {

  private final Option<A> option1;

  OptionStep1(Option<A> option1) {
    this.option1 = option1;
  }

  public <B> OptionStep2<A, B> then(Function<? super A, ? extends Option<? extends B>> functor) {
    Option<B> option2 = option1.flatMap(functor);
    return new OptionStep2<>(option1, option2);
  }

  public <B> OptionStep2<A, B> then(Supplier<? extends Option<? extends B>> supplier) {
    Option<B> option2 = option1.flatMap(value1 -> supplier.get());
    return new OptionStep2<>(option1, option2);
  }

  public OptionStep1<A> filter(Predicate<? super A> predicate) {
    Option<A> filterOption1 = option1.filter(predicate);
    return new OptionStep1<>(filterOption1);
  }

  public <Z> Option<Z> yield(Function<? super A, Z> functor) {
    return option1.map(functor);
  }

}
