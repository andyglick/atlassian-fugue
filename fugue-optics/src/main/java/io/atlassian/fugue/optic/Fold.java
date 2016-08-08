package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.Monoids;
import io.atlassian.fugue.Option;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@link Fold} can be seen as a {@link Getter} with many targets or a weaker
 * {@link PTraversal} which cannot modify its target.
 * <p>
 * {@link Fold} is on the top of the Optic hierarchy which means that
 * {@link Getter}, {@link PTraversal}, {@link POptional}, {@link PLens},
 * {@link PPrism} and {@link PIso} are valid {@link Fold}
 *
 * @param <S> the source of a {@link Fold}
 * @param <A> the target of a {@link Fold}
 */
public abstract class Fold<S, A> {

  /**
   * map each target to a {@link Monoid} and combine the results underlying
   * representation of {@link Fold}, all {@link Fold} methods are defined in
   * terms of foldMap
   */
  public abstract <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f);

  /**
   * combine all targets using a target's {@link Monoid}
   */
  public final Function<S, A> fold(Monoid<A> monoid) {
    return foldMap(monoid, Function.identity());
  }

  /**
   * get all the targets of a {@link Fold}
   */
  public final Iterable<A> getAll(final S s) {
    return foldMap(Monoids.iterable(), Collections::singleton).apply(s);
  }

  /**
   * find the first target of a {@link Fold} matching the predicate
   */
  public final Function<S, Option<A>> find(final Predicate<A> p) {
    return foldMap(Monoids.firstOption(), a -> p.test(a) ? Option.some(a) : Option.none());
  }

  /**
   * get the first target of a {@link Fold}
   */
  public final Option<A> headOption(final S s) {
    return find(__ -> true).apply(s);
  }

  /**
   * check if at least one target satisfies the predicate
   */
  public final Predicate<S> exist(final Predicate<A> p) {
    return foldMap(Monoids.disjunction, p::test)::apply;
  }

  /**
   * check if all targets satisfy the predicate
   */
  public final Function<S, Boolean> all(final Predicate<A> p) {
    return foldMap(Monoids.conjunction, p::test)::apply;
  }

  /**
   * join two {@link Fold} with the same target
   */
  public final <S1> Fold<Either<S, S1>, A> sum(final Fold<S1, A> other) {
    return new Fold<Either<S, S1>, A>() {
      @Override public <B> Function<Either<S, S1>, B> foldMap(final Monoid<B> monoid, final Function<A, B> f) {
        return s -> s.fold(Fold.this.foldMap(monoid, f), other.foldMap(monoid, f));
      }
    };
  }

  /**********************************************************/
  /** Compose methods between a {@link Fold} and another Optics */
  /**********************************************************/

  /**
   * compose a {@link Fold} with a {@link Fold}
   */
  public final <B> Fold<S, B> composeFold(final Fold<A, B> other) {
    return new Fold<S, B>() {
      @Override public <C> Function<S, C> foldMap(final Monoid<C> monoid, final Function<B, C> f) {
        return Fold.this.foldMap(monoid, other.foldMap(monoid, f));
      }
    };
  }

  /**
   * compose a {@link Fold} with a {@link Getter}
   */
  public final <C> Fold<S, C> composeGetter(final Getter<A, C> other) {
    return composeFold(other.asFold());
  }

  /**
   * compose a {@link Fold} with a {@link POptional}
   */
  public final <B, C, D> Fold<S, C> composeOptional(final POptional<A, B, C, D> other) {
    return composeFold(other.asFold());
  }

  /**
   * compose a {@link Fold} with a {@link PPrism}
   */
  public final <B, C, D> Fold<S, C> composePrism(final PPrism<A, B, C, D> other) {
    return composeFold(other.asFold());
  }

  /**
   * compose a {@link Fold} with a {@link PLens}
   */
  public final <B, C, D> Fold<S, C> composeLens(final PLens<A, B, C, D> other) {
    return composeFold(other.asFold());
  }

  /**
   * compose a {@link Fold} with a {@link PIso}
   */
  public final <B, C, D> Fold<S, C> composeIso(final PIso<A, B, C, D> other) {
    return composeFold(other.asFold());
  }

  public static <A> Fold<A, A> id() {
    return PIso.<A, A> pId().asFold();
  }

  public static <A> Fold<Either<A, A>, A> codiagonal() {
    return new Fold<Either<A, A>, A>() {
      @Override public <B> Function<Either<A, A>, B> foldMap(final Monoid<B> monoid, final Function<A, B> f) {
        return e -> e.fold(f, f);
      }
    };
  }

}
