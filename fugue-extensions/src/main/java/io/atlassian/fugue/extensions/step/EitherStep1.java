package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The first step of the {@link Either} type.
 *
 * This class is not intended to be contructed manually, and should only be used as part of a
 * {@link Steps} chain, started by {@link Steps#begin(Either)}
 *
 * @param <A> The right hand side type of the first defined right value
 * @param <LEFT> The left hand side type of the Either result
 * @see Steps for usage examples
 * @since 4.7.0
 */
public class EitherStep1<A, LEFT> {

  private final Either<LEFT, A> either1;

  EitherStep1(Either<LEFT, A> either1) {
    this.either1 = either1;
  }

  /**
   * Apply the provided function with the previous Step results.
   *
   * Internally this will perform a {@link Either#flatMap(Function)} and the result
   * will become the next step value.
   *
   * @param functor The functor to be applied as a flatMap with the previous step
   * @param <B> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to {@link LEFT}
   * @return The next step class
   */
  public <B, LL extends LEFT> EitherStep2<A, B, LEFT> then(Function<? super A, Either<LL, B>> functor) {
    Either<LEFT, B> either2 = either1.flatMap(functor);
    return new EitherStep2<>(either1, either2);
  }

  /**
   * Apply the provided supplier with the previous Step results.
   *
   * Internally this will perform a {@link Either#flatMap(Function)} and the supplier
   * will become the next step value.
   *
   * This is different to {@link #then(Function)} in that the previous step results are
   * not provided for the new step evaluation.
   *
   * @param supplier The supplier to provide the result of the flatMap with the previous step.
   * @param <B> The right hand side type of the next step result
   * @param <LL> The left hand side type of the result that must be related to {@link LEFT}
   * @return The next step class
   */
  public <B, LL extends LEFT> EitherStep2<A, B, LEFT> then(Supplier<Either<LL, B>> supplier) {
    Either<LEFT, B> either2 = either1.flatMap(value1 -> supplier.get());
    return new EitherStep2<>(either1, either2);
  }

  /**
   * Apply the provided predicate with the previous step results.
   *
   * If the 
   *
   * @param predicate
   * @param unsatisfiedHandler
   * @return
   */
  public EitherStep1<A, LEFT> filter(Predicate<? super A> predicate,
    Function<Option<LEFT>, ? extends Either<? extends LEFT, ? extends A>> unsatisfiedHandler) {
    Either<LEFT, A> filterEither1 = either1.filter(predicate, unsatisfiedHandler);
    return new EitherStep1<>(filterEither1);
  }

  public <Z> Either<LEFT, Z> yield(Function<? super A, Z> functor) {
    return either1.map(functor);
  }

}
