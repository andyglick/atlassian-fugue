package io.atlassian.fugue;

import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * {@link Collector} instances.
 *
 * @since 4.8.0
 */
public final class FugueCollectors {

  /**
   * Collect the left values if there are only lefts, otherwise return the first
   * right encountered. Collectors have `foldLeft` semantics. For
   * {@link java.util.Spliterator#ORDERED} sequences the left-most
   * {@link io.atlassian.fugue.Either.Right} value is returned.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @since 4.8.0
   * @return left biased collector of {@link Either}.
   */
  public static <L, R> Collector<Either<L, R>, ?, Either<List<L>, R>> toEitherLeft() {
    return toEitherLeft(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Collect the left values if there are only lefts, otherwise return the first
   * right encountered. Collectors have `foldLeft` semantics. For
   * {@link java.util.Spliterator#ORDERED} sequences the left-most
   * {@link io.atlassian.fugue.Either.Right} value is returned.
   *
   * @param lCollector result collector
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param <A> the mutable accumulation type of the reduction operation
   * @param <B> the result type of the reduction operation
   * @since 4.8.0
   * @return left biased collector of {@link Either}.
   */
  public static <L, R, A, B> Collector<Either<L, R>, ?, Either<B, R>> toEitherLeft(Collector<L, A, B> lCollector) {
    requireNonNull(lCollector);
    return Collector.of(() -> new Ref<>(Either.<A, R> left(lCollector.supplier().get())), (ref, either) -> ref.update(acc -> acc.left().flatMap(
      a -> either.leftMap(l -> {
        lCollector.accumulator().accept(a, l);
        return a;
      }))), (refL, refR) -> new Ref<>(refL.get().left().flatMap(lv -> refR.get().leftMap(rv -> lCollector.combiner().apply(lv, rv)))), ref -> ref
      .get().leftMap(a -> lCollector.finisher().apply(a)), maybeUnorderedCharacteristics(lCollector));
  }

  /**
   * Collect the right values if there are only rights, otherwise return the
   * first left encountered. Collectors have `foldLeft` semantics. For
   * {@link java.util.Spliterator#ORDERED} sequences the left-most
   * {@link io.atlassian.fugue.Either.Left} value is returned.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @since 4.8.0
   * @return right biased collector of {@link Either}.
   */
  public static <L, R> Collector<Either<L, R>, ?, Either<L, List<R>>> toEitherRight() {
    return toEitherRight(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Collect the right values if there are only rights, otherwise return the
   * first left encountered. Collectors have `foldLeft` semantics. For
   * {@link java.util.Spliterator#ORDERED} sequences the left-most
   * {@link io.atlassian.fugue.Either.Left} value is returned.
   *
   * @param rCollector result collector
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param <A> the mutable accumulation type of the reduction operation
   * @param <B> the result type of the reduction operation
   * @since 4.8.0
   * @return right biased collector of {@link Either}.
   */
  public static <L, R, A, B> Collector<Either<L, R>, ?, Either<L, B>> toEitherRight(Collector<R, A, B> rCollector) {
    requireNonNull(rCollector);
    return Collector.of(() -> new Ref<>(Either.<L, A> right(rCollector.supplier().get())),
      (ref, either) -> ref.update(acc -> acc.flatMap(a -> either.map(r -> {
        rCollector.accumulator().accept(a, r);
        return a;
      }))), (refL, refR) -> new Ref<>(refL.get().flatMap(lv -> refR.get().map(rv -> rCollector.combiner().apply(lv, rv)))),
      ref -> ref.get().map(a -> rCollector.finisher().apply(a)), maybeUnorderedCharacteristics(rCollector));
  }

  /**
   * Collect the values wrapped within the option.
   *
   * @param <A> the option type
   * @since 4.8.0
   * @return flattening collector of {@link Option}.
   */
  public static <A> Collector<Option<A>, ?, List<A>> flatten() {
    return flatten(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Collect the values wrapped within the option.
   *
   * @param aCollector result collector
   * @param <A> the option type
   * @param <B> the mutable accumulation type of the reduction operation
   * @param <C> the result type of the reduction operation
   * @since 4.8.0
   * @return flattening collector of {@link Option}.
   */
  public static <A, B, C> Collector<Option<A>, ?, C> flatten(Collector<A, B, C> aCollector) {
    requireNonNull(aCollector);
    return Collector.of(aCollector.supplier(), (acc, option) -> option.forEach(a -> aCollector.accumulator().accept(acc, a)), aCollector.combiner(),
      aCollector.finisher(), aCollector.characteristics().toArray(new Collector.Characteristics[0]));
  }

  /**
   * Collect the right values if there are only successes, otherwise return the
   * first failure encountered. Collectors have `foldLeft` semantics. For
   * {@link java.util.Spliterator#ORDERED} sequences the left-most
   * {@code io.atlassian.fugue.Try.Failure} value is returned.
   *
   * @param <A> the success type
   * @since 4.8.0
   * @return success biased collector of {@link Try}.
   */
  public static <A> Collector<Try<A>, ?, Try<List<A>>> toTrySuccess() {
    return toTrySuccess(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Collect the right values if there are only successes, otherwise return the
   * first failure encountered. Collectors have `foldLeft` semantics. For
   * {@link java.util.Spliterator#ORDERED} sequences the left-most
   * {@code io.atlassian.fugue.Try.Failure} value is returned.
   *
   * @param <A> the success type
   * @param <B> the mutable accumulation type of the reduction operation
   * @param <C> the result type of the reduction operation
   * @since 4.8.0
   * @return success biased collector of {@link Try}.
   */
  public static <A, B, C> Collector<Try<A>, ?, Try<C>> toTrySuccess(Collector<A, B, C> aCollector) {
    requireNonNull(aCollector);
    return Collector.of(() -> new Ref<>(Checked.now(() -> requireNonNull(aCollector.supplier().get()))),
      (ref, aTry) -> ref.update(acc -> acc.flatMap(b -> aTry.map(a -> {
        aCollector.accumulator().accept(b, a);
        return b;
      }))), (refL, refR) -> new Ref<>(refL.get().flatMap(lv -> refR.get().map(rv -> aCollector.combiner().apply(lv, rv)))),
      ref -> ref.get().map(b -> aCollector.finisher().apply(b)), maybeUnorderedCharacteristics(aCollector));
  }

  private static Collector.Characteristics[] maybeUnorderedCharacteristics(Collector<?, ?, ?> delegate) {
    return delegate.characteristics().contains(Collector.Characteristics.UNORDERED) ? new Collector.Characteristics[] { Collector.Characteristics.UNORDERED }
      : new Collector.Characteristics[0];
  }

  /**
   * Mutable reference. Used to carry collectors state. Specifically for
   * {@link Collector#accumulator()} because it should always mutate the
   * collectors state in-place. Not thread safe!
   * {@link java.util.stream.Collector.Characteristics#CONCURRENT} must not be
   * present in the list of collector's characteristics.
   *
   * @param <A> reference type
   */
  private static final class Ref<A> {
    private A value;

    private Ref(A value) {
      this.value = value;
    }

    private A get() {
      return value;
    }

    private void update(UnaryOperator<A> update) {
      value = update.apply(value);
    }
  }

}
