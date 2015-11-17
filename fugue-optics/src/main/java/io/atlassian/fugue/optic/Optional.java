package io.atlassian.fugue.optic;

import io.atlassian.fugue.*;
import io.atlassian.fugue.optic.internal.Utils;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * {@link POptional} restricted to monomorphic update
 */
public final class Optional<S, A> extends POptional<S, S, A, A> {

  final POptional<S, S, A, A> pOptional;

  public Optional(final POptional<S, S, A, A> pOptional) {
    this.pOptional = pOptional;
  }

  @Override public Function<S, S> set(final A a) {
    return pOptional.set(a);
  }

  @Override public Function<S, Stream<S>> modifyStreamF(final Function<A, Stream<A>> f) {
    return pOptional.modifyStreamF(f);
  }

  @Override public Function<S, Supplier<S>> modifySupplierF(final Function<A, Supplier<A>> f) {
    return pOptional.modifySupplierF(f);
  }

  @Override public Function<S, Option<S>> modifyOptionF(final Function<A, Option<A>> f) {
    return pOptional.modifyOptionF(f);
  }

  @Override public <C> Function<S, Function<C, S>> modifyFunctionF(final Function<A, Function<C, A>> f) {
    return pOptional.modifyFunctionF(f);
  }

  @Override public <L> Function<S, Either<L, S>> modifyEitherF(final Function<A, Either<L, A>> f) {
    return pOptional.modifyEitherF(f);
  }

  @Override public Function<S, Iterable<S>> modifyIterableF(final Function<A, Iterable<A>> f) {
    return pOptional.modifyIterableF(f);
  }

  @Override public Function<S, Pair<S, S>> modifyPairF(final Function<A, Pair<A, A>> f) {
    return pOptional.modifyPairF(f);
  }

  @Override public Function<S, S> modify(final Function<A, A> f) {
    return pOptional.modify(f);
  }

  @Override public Either<S, A> getOrModify(final S s) {
    return pOptional.getOrModify(s);
  }

  @Override public Option<A> getOption(final S s) {
    return pOptional.getOption(s);
  }

  /**
   * join two {@link Optional} with the same target
   */
  public final <S1> Optional<Either<S, S1>, A> sum(final Optional<S1, A> other) {
    return new Optional<>(pOptional.sum(other.pOptional));
  }

  @Override public final <C> Optional<Pair<S, C>, Pair<A, C>> first() {
    return new Optional<>(pOptional.first());
  }

  @Override public final <C> Optional<Pair<C, S>, Pair<C, A>> second() {
    return new Optional<>(pOptional.second());
  }

  /**************************************************************/
  /** Compose methods between a {@link Optional} and another Optics */
  /**************************************************************/

  /**
   * compose a {@link Optional} with a {@link Setter}
   */
  public final <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return new Setter<>(pOptional.composeSetter(other.pSetter));
  }

  /**
   * compose a {@link Optional} with a {@link Traversal}
   */
  public final <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return new Traversal<>(pOptional.composeTraversal(other.pTraversal));
  }

  /**
   * compose a {@link Optional} with a {@link Optional}
   */
  public final <C> Optional<S, C> composeOptional(final Optional<A, C> other) {
    return new Optional<>(pOptional.composeOptional(other.pOptional));
  }

  /**
   * compose a {@link Optional} with a {@link Prism}
   */
  public final <C> Optional<S, C> composePrism(final Prism<A, C> other) {
    return new Optional<>(pOptional.composePrism(other.pPrism));
  }

  /**
   * compose a {@link Optional} with a {@link Lens}
   */
  public final <C> Optional<S, C> composeLens(final Lens<A, C> other) {
    return new Optional<>(pOptional.composeLens(other.pLens));
  }

  /**
   * compose a {@link Optional} with an {@link Iso}
   */
  public final <C> Optional<S, C> composeIso(final Iso<A, C> other) {
    return new Optional<>(pOptional.composeIso(other.pIso));
  }

  /********************************************************************/
  /** Transformation methods to view a {@link Optional} as another Optics */
  /********************************************************************/

  /**
   * view a {@link Optional} as a {@link Setter}
   */
  @Override public final Setter<S, A> asSetter() {
    return new Setter<>(pOptional.asSetter());
  }

  /**
   * view a {@link Optional} as a {@link Traversal}
   */
  @Override public final Traversal<S, A> asTraversal() {
    return new Traversal<>(pOptional.asTraversal());
  }

  public static <S> Optional<S, S> id() {
    return new Optional<>(POptional.pId());
  }

  public static <S, A> Optional<S, A> optional(final Function<S, Option<A>> getOption, final Function<A, Function<S, S>> set) {
    return new Optional<>(new POptional<S, S, A, A>() {

      @Override public Either<S, A> getOrModify(final S s) {
        return getOption.apply(s).fold(() -> Either.<S, A> left(s), Eithers.<S, A> toRight());
      }

      @Override public Function<S, S> set(final A a) {
        return set.apply(a);
      }

      @Override public Option<A> getOption(final S s) {
        return getOption.apply(s);
      }

      @Override public <C> Function<S, Function<C, S>> modifyFunctionF(final Function<A, Function<C, A>> f) {
        return s -> getOption.apply(s).<Function<C, S>> fold(() -> __ -> s, a -> f.apply(a).andThen(b -> set.apply(b).apply(s)));
      }

      @Override public <L> Function<S, Either<L, S>> modifyEitherF(final Function<A, Either<L, A>> f) {
        return s -> getOption.apply(s).<Either<L, S>> fold(() -> Either.right(s), t -> f.apply(t).right().map(b -> set.apply(b).apply(s)));
      }

      @Override public Function<S, Option<S>> modifyOptionF(final Function<A, Option<A>> f) {
        return s -> getOption.apply(s).fold(() -> Option.some(s), t -> f.apply(t).map(b -> set.apply(b).apply(s)));
      }

      @Override public Function<S, Stream<S>> modifyStreamF(final Function<A, Stream<A>> f) {
        return s -> getOption.apply(s).fold(() -> Stream.of(s), t -> f.apply(t).map(b -> set.apply(b).apply(s)));
      }

      @Override public Function<S, Iterable<S>> modifyIterableF(final Function<A, Iterable<A>> f) {
        return s -> getOption.apply(s).<Iterable<S>> fold(() -> Collections.singleton(s), t -> Iterables.map(f.apply(t), b -> set.apply(b).apply(s)));
      }

      @Override public Function<S, Supplier<S>> modifySupplierF(final Function<A, Supplier<A>> f) {
        return s -> getOption.apply(s).<Supplier<S>> fold(() -> Suppliers.ofInstance(s),
          t -> Suppliers.compose(b -> set.apply(b).apply(s), f.apply(t)));
      }

      @Override public Function<S, Pair<S, S>> modifyPairF(final Function<A, Pair<A, A>> f) {
        return s -> getOption.apply(s).<Pair<S, S>> fold(() -> Pair.pair(s, s), t -> Utils.map(f.apply(t), b -> set.apply(b).apply(s)));
      }

      @Override public Function<S, S> modify(final Function<A, A> f) {
        return s -> getOption.apply(s).fold(() -> s, a -> set.apply(f.apply(a)).apply(s));
      }

    });
  }

}
