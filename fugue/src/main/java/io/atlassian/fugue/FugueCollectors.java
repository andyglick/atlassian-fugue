package io.atlassian.fugue;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * {@link Collector} instances.
 *
 * @since 4.8.0
 */
public final class FugueCollectors {

  /**
   * Collect the left values if there are only lefts, otherwise return the first
   * right encountered.
   *
   * @param <A> the LHS type
   * @param <B> the RHS type
   * @since 4.8.0
   * @return left biased collector of {@link Either}.
   */
  public static <A, B> Collector<Either<A, B>, ?, Either<List<A>, B>> toEitherLeft() {
    return toEitherLeft(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Collect the left values if there are only lefts, otherwise return the first
   * right encountered.
   *
   * @param lCollector result collector
   * @param <A> the LHS type
   * @param <B> the RHS type
   * @param <C> the mutable accumulation type of the reduction operation
   * @param <D> the result type of the reduction operation
   * @since 4.8.0
   * @return left biased collector of {@link Either}.
   */
  public static <A, B, C, D> Collector<Either<A, B>, ?, Either<D, B>> toEitherLeft(Collector<A, C, D> lCollector) {
    return new EitherLeftCollector<>(lCollector);
  }

  /**
   * Collect the right values if there are only rights, otherwise return the
   * first left encountered.
   *
   * @param <A> the LHS type
   * @param <B> the RHS type
   * @since 4.8.0
   * @return right biased collector of {@link Either}.
   */
  public static <A, B> Collector<Either<A, B>, ?, Either<A, List<B>>> toEitherRight() {
    return toEitherRight(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Collect the right values if there are only rights, otherwise return the
   * first left encountered.
   *
   * @param rCollector result collector
   * @param <A> the LHS type
   * @param <B> the RHS type
   * @param <C> the mutable accumulation type of the reduction operation
   * @param <D> the result type of the reduction operation
   * @since 4.8.0
   * @return right biased collector of {@link Either}.
   */
  public static <A, B, C, D> Collector<Either<A, B>, ?, Either<A, D>> toEitherRight(Collector<B, C, D> rCollector) {
    return new EitherRightCollector<>(rCollector);
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
    return new OptionFlattenCollector<>(aCollector);
  }

  /**
   * Collect the right values if there are only successes, otherwise return the
   * first failure encountered.
   *
   * @param <A> the success type
   * @since 4.8.0
   * @return right biased collector of {@link Either}.
   */
  public static <A> Collector<Try<A>, ?, Try<List<A>>> toTrySuccess() {
    return toTrySuccess(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }

  /**
   * Collect the right values if there are only successes, otherwise return the
   * first failure encountered.
   *
   * @param <A> the success type
   * @param <B> the mutable accumulation type of the reduction operation
   * @param <C> the result type of the reduction operation
   * @since 4.8.0
   * @return right biased collector of {@link Either}.
   */
  public static <A, B, C> Collector<Try<A>, ?, Try<C>> toTrySuccess(Collector<A, B, C> aCollector) {
    return new TrySuccessCollector<>(aCollector);
  }

  private static final class EitherRightCollector<A, B, C, D> implements Collector<Either<A, B>, Ref<Either<A, C>>, Either<A, D>> {
    private final Collector<B, C, D> delegate;

    private EitherRightCollector(Collector<B, C, D> delegate) {
      this.delegate = delegate;
    }

    @Override public Supplier<Ref<Either<A, C>>> supplier() {
      return () -> new Ref<>(Either.right(delegate.supplier().get()));
    }

    @Override public BiConsumer<Ref<Either<A, C>>, Either<A, B>> accumulator() {
      return (ref, either) -> ref.set(ref.get().flatMap(b -> either.map(r -> {
        delegate.accumulator().accept(b, r);
        return b;
      })));
    }

    @Override public BinaryOperator<Ref<Either<A, C>>> combiner() {
      return (refL, refR) -> new Ref<>(refL.get().flatMap(lb -> refR.get().map(rb -> delegate.combiner().apply(lb, rb))));
    }

    @Override public Function<Ref<Either<A, C>>, Either<A, D>> finisher() {
      return ref -> ref.get().map(b -> delegate.finisher().apply(b));
    }

    @Override public Set<Characteristics> characteristics() {
      return Collections.emptySet();
    }
  }

  private static final class EitherLeftCollector<A, B, C, D> implements Collector<Either<A, B>, Ref<Either<C, B>>, Either<D, B>> {
    private final Collector<A, C, D> delegate;

    private EitherLeftCollector(Collector<A, C, D> delegate) {
      this.delegate = delegate;
    }

    @Override public Supplier<Ref<Either<C, B>>> supplier() {
      return () -> new Ref<>(Either.left(delegate.supplier().get()));
    }

    @Override public BiConsumer<Ref<Either<C, B>>, Either<A, B>> accumulator() {
      return (ref, either) -> ref.set(ref.get().left().flatMap(b -> either.leftMap(l -> {
        delegate.accumulator().accept(b, l);
        return b;
      })));
    }

    @Override public BinaryOperator<Ref<Either<C, B>>> combiner() {
      return (refL, refR) -> new Ref<>(refL.get().left().flatMap(lb -> refR.get().leftMap(rb -> delegate.combiner().apply(lb, rb))));
    }

    @Override public Function<Ref<Either<C, B>>, Either<D, B>> finisher() {
      return ref -> ref.get().leftMap(b -> delegate.finisher().apply(b));
    }

    @Override public Set<Characteristics> characteristics() {
      return Collections.emptySet();
    }
  }

  private static final class OptionFlattenCollector<A, B, C> implements Collector<Option<A>, B, C> {
    private final Collector<A, B, C> delegate;

    private OptionFlattenCollector(Collector<A, B, C> delegate) {
      this.delegate = delegate;
    }

    @Override public Supplier<B> supplier() {
      return () -> delegate.supplier().get();
    }

    @Override public BiConsumer<B, Option<A>> accumulator() {
      return (b, option) -> option.forEach(a -> delegate.accumulator().accept(b, a));
    }

    @Override public BinaryOperator<B> combiner() {
      return (bl, br) -> delegate.combiner().apply(bl, br);
    }

    @Override public Function<B, C> finisher() {
      return b -> delegate.finisher().apply(b);
    }

    @Override public Set<Characteristics> characteristics() {
      return Collections.emptySet();
    }
  }

  private static final class TrySuccessCollector<A, B, C> implements Collector<Try<A>, Ref<Try<B>>, Try<C>> {
    private final Collector<A, B, C> delegate;

    private TrySuccessCollector(Collector<A, B, C> delegate) {
      this.delegate = delegate;
    }

    @Override public Supplier<Ref<Try<B>>> supplier() {
      return () -> new Ref<>(Try.successful(delegate.supplier().get()));
    }

    @Override public BiConsumer<Ref<Try<B>>, Try<A>> accumulator() {
      return (ref, aTry) -> ref.set(ref.get().flatMap(b -> aTry.map(a -> {
        delegate.accumulator().accept(b, a);
        return b;
      })));
    }

    @Override public BinaryOperator<Ref<Try<B>>> combiner() {
      return (refL, refR) -> new Ref<>(refL.get().flatMap(lb -> refR.get().map(rb -> delegate.combiner().apply(lb, rb))));
    }

    @Override public Function<Ref<Try<B>>, Try<C>> finisher() {
      return ref -> ref.get().map(b -> delegate.finisher().apply(b));
    }

    @Override public Set<Characteristics> characteristics() {
      return Collections.emptySet();
    }
  }

  private static final class Ref<A> {
    private A value;

    private Ref(A value) {
      this.value = value;
    }

    private A get() {
      return value;
    }

    private void set(A value) {
      this.value = value;
    }
  }

}
