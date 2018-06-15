package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Try;

import java.util.Optional;

public final class Steps {

  private Steps() {
    // do not instantiate
  }

  public static <E1, E> EitherStep1<E1, E> begin(Either<E, E1> either) {
    return new EitherStep1<>(either);
  }

  public static <E> OptionStep1<E> begin(Option<E> option) {
    return new OptionStep1<>(option);
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType") public static <E> OptionalStep1<E> begin(Optional<E> option) {
    return new OptionalStep1<>(option);
  }

  public static <E> TryStep1<E> begin(Try<E> aTry) {
    return new TryStep1<>(aTry);
  }

}
