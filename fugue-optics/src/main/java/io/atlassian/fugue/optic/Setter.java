package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;

import java.util.function.Function;

/**
 * {@link PSetter} with a monomorphic modify function
 */
public final class Setter<S, A> extends PSetter<S, S, A, A> {

  final PSetter<S, S, A, A> pSetter;

  public Setter(final PSetter<S, S, A, A> pSetter) {
    this.pSetter = pSetter;
  }

  @Override public Function<S, S> modify(final Function<A, A> f) {
    return pSetter.modify(f);
  }

  @Override public Function<S, S> set(final A b) {
    return pSetter.set(b);
  }

  /**
   * join two {@link Setter} with the same target
   */
  public final <S1> Setter<Either<S, S1>, A> sum(final Setter<S1, A> other) {
    return new Setter<>(pSetter.sum(other.pSetter));
  }

  /************************************************************/
  /** Compose methods between a {@link Setter} and another Optics */
  /************************************************************/

  /**
   * compose a {@link Setter} with a {@link Setter}
   */
  public final <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return new Setter<>(pSetter.composeSetter(other.pSetter));
  }

  /**
   * compose a {@link Setter} with a {@link Traversal}
   */
  public final <C> Setter<S, C> composeTraversal(final Traversal<A, C> other) {
    return new Setter<>(pSetter.composeTraversal(other.pTraversal));
  }

  /**
   * compose a {@link Setter} with an {@link Iso}
   */
  public final <C> Setter<S, C> composeIso(final Iso<A, C> other) {
    return new Setter<>(pSetter.composeIso(other.pIso));
  }

  public static <S> Setter<S, S> id() {
    return new Setter<>(pId());
  }

  public static <S> Setter<Either<S, S>, S> codiagonal() {
    return new Setter<>(pCodiagonal());
  }

  /**
   * alias for {@link PSetter} constructor with a monomorphic modify function
   */
  public static <S, A> Setter<S, A> setter(final Function<Function<A, A>, Function<S, S>> modify) {
    return new Setter<>(pSetter(modify));
  }
}
