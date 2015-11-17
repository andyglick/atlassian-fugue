package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Traversal<S, A> extends PTraversal<S, S, A, A> {

  final PTraversal<S, S, A, A> pTraversal;

  public Traversal(final PTraversal<S, S, A, A> pTraversal) {
    this.pTraversal = pTraversal;
  }

  @Override public <C> Function<S, Function<C, S>> modifyFunctionF(final Function<A, Function<C, A>> f) {
    return pTraversal.modifyFunctionF(f);
  }

  @Override public <L> Function<S, Either<L, S>> modifyEitherF(final Function<A, Either<L, A>> f) {
    return pTraversal.modifyEitherF(f);
  }

  @Override public Function<S, Option<S>> modifyOptionF(final Function<A, Option<A>> f) {
    return pTraversal.modifyOptionF(f);
  }

  @Override public Function<S, Stream<S>> modifyStreamF(final Function<A, Stream<A>> f) {
    return pTraversal.modifyStreamF(f);
  }

  @Override public Function<S, Iterable<S>> modifyIterableF(final Function<A, Iterable<A>> f) {
    return pTraversal.modifyIterableF(f);
  }

  @Override public Function<S, Supplier<S>> modifySupplierF(final Function<A, Supplier<A>> f) {
    return pTraversal.modifySupplierF(f);
  }

  @Override public Function<S, Pair<S, S>> modifyPairF(final Function<A, Pair<A, A>> f) {
    return pTraversal.modifyPairF(f);
  }

  @Override public <M> Function<S, M> foldMap(final Supplier<M> identity, final BinaryOperator<M> op, final Function<A, M> f) {
    return pTraversal.foldMap(identity, op, f);
  }

  /**
   * join two {@link Traversal} with the same target
   */
  public final <S1> Traversal<Either<S, S1>, A> sum(final Traversal<S1, A> other) {
    return new Traversal<>(pTraversal.sum(other.pTraversal));
  }

  /***************************************************************/
  /** Compose methods between a {@link Traversal} and another Optics */
  /***************************************************************/

  /**
   * compose a {@link Traversal} with a {@link Setter}
   */
  public final <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return new Setter<>(pTraversal.composeSetter(other.pSetter));
  }

  /**
   * compose a {@link Traversal} with a {@link Traversal}
   */
  public final <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return new Traversal<>(pTraversal.composeTraversal(other.pTraversal));
  }

  /*********************************************************************/
  /** Transformation methods to view a {@link Traversal} as another Optics */
  /*********************************************************************/

  /**
   * view a {@link Traversal} as a {@link Setter}
   */
  @Override public final Setter<S, A> asSetter() {
    return new Setter<>(pTraversal.asSetter());
  }

  public static <S> Traversal<S, S> id() {
    return new Traversal<>(pId());
  }

  public static <S> Traversal<Either<S, S>, S> codiagonal() {
    return new Traversal<>(pCodiagonal());
  }

  public static <S, A> Traversal<S, A> traversal(final Function<S, A> get1, final Function<S, A> get2, final BiFunction<A, A, Function<S, S>> set) {
    return new Traversal<>(pTraversal(get1, get2, set));
  }

  public static <S, A> Traversal<S, A> traversal(final Function<S, A> get1, final Function<S, A> get2, final Function<S, A> get3,
    final Function<A, Function<A, Function<A, Function<S, S>>>> set) {
    return new Traversal<>(pTraversal(get1, get2, get3, set));
  }

  public static <S, A> Traversal<S, A> traversal(final Function<S, A> get1, final Function<S, A> get2, final Function<S, A> get3,
    final Function<S, A> get4, final Function<A, Function<A, Function<A, Function<A, Function<S, S>>>>> set) {
    return new Traversal<>(pTraversal(get1, get2, get3, get4, set));
  }

  public static <S, A> Traversal<S, A> traversal(final Function<S, A> get1, final Function<S, A> get2, final Function<S, A> get3,
    final Function<S, A> get4, final Function<S, A> get5, final Function<A, Function<A, Function<A, Function<A, Function<A, Function<S, S>>>>>> set) {
    return new Traversal<>(pTraversal(get1, get2, get3, get4, get5, set));
  }

  public static <S, A> Traversal<S, A> traversal(final Function<S, A> get1, final Function<S, A> get2, final Function<S, A> get3,
    final Function<S, A> get4, final Function<S, A> get5, final Function<S, A> get6,
    final Function<A, Function<A, Function<A, Function<A, Function<A, Function<A, Function<S, S>>>>>>> set) {
    return new Traversal<>(pTraversal(get1, get2, get3, get4, get5, get6, set));
  }

}
