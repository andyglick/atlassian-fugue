package io.atlassian.fugue.optic;

import io.atlassian.fugue.*;

import java.util.Collections;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A {@link POptional} can be seen as a pair of functions: - `getOrModify: S =>
 * T \/ A` - `set : (B, S) => T`
 * <p/>
 * A {@link POptional} could also be defined as a weaker {@link PLens} and
 * weaker {@link PPrism}
 * <p/>
 * {@link POptional} stands for Polymorphic Optional as it set and modify
 * methods change a type `A` to `B` and `S` to `T`. {@link Optional} is a
 * {@link POptional} restricted to monomorphic updates: {{{ type Optional[S, A]
 * = POptional[S, S, A, A] }}}
 *
 * @param <S> the source of a {@link POptional}
 * @param <T> the modified source of a {@link POptional}
 * @param <A> the target of a {@link POptional}
 * @param <B> the modified target of a {@link POptional}
 */
public abstract class POptional<S, T, A, B> {

  POptional() {
    super();
  }

  /**
   * get the target of a {@link POptional} or modify the source in case there is
   * no target
   */
  public abstract Either<T, A> getOrModify(S s);

  /**
   * get the modified source of a {@link POptional}
   */
  public abstract Function<S, T> set(final B b);

  /**
   * get the target of a {@link POptional} or nothing if there is no target
   */
  public abstract Option<A> getOption(final S s);

  /**
   * modify polymorphically the target of a {@link POptional} with an
   * Applicative function
   */
  public abstract <C> Function<S, Function<C, T>> modifyFunctionF(final Function<A, Function<C, B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an
   * Applicative function
   */
  public abstract <L> Function<S, Either<L, T>> modifyEitherF(final Function<A, Either<L, B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an
   * Applicative function
   */
  public abstract Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an
   * Applicative function
   */
  public abstract Function<S, Pair<T, T>> modifyPairF(final Function<A, Pair<B, B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an
   * Applicative function
   */
  public abstract Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an
   * Applicative function
   */
  public abstract Function<S, Iterable<T>> modifyIterableF(final Function<A, Iterable<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with a function
   */
  public abstract Function<S, T> modify(final Function<A, B> f);

  /**
   * modify polymorphically the target of a {@link POptional} with a function.
   * return empty if the {@link POptional} is not matching
   */
  public final Function<S, Option<T>> modifyOption(final Function<A, B> f) {
    return s -> getOption(s).map(__ -> modify(f).apply(s));
  }

  /**
   * set polymorphically the target of a {@link POptional} with a value. return
   * empty if the {@link POptional} is not matching
   */
  public final Function<S, Option<T>> setOption(final B b) {
    return modifyOption(__ -> (b));
  }

  /**
   * check if a {@link POptional} has a target
   */
  public final boolean isMatching(final S s) {
    return getOption(s).isDefined();

  }

  /**
   * join two {@link POptional} with the same target
   */
  public final <S1, T1> POptional<Either<S, S1>, Either<T, T1>, A, B> sum(final POptional<S1, T1, A, B> other) {
    return pOptional(e -> e.fold(s -> getOrModify(s).left().map(Eithers.toLeft()), s1 -> other.getOrModify(s1).left().map(Eithers.toRight())),
      b -> e -> e.bimap(set(b), other.set(b)));
  }

  public <C> POptional<Pair<S, C>, Pair<T, C>, Pair<A, C>, Pair<B, C>> first() {
    return pOptional(sc -> getOrModify(sc.left()).bimap(t -> Pair.pair(t, sc.right()), a -> Pair.pair(a, sc.right())),
      bc -> s_ -> Pair.pair(set(bc.left()).apply(s_.left()), bc.right()));
  }

  public <C> POptional<Pair<C, S>, Pair<C, T>, Pair<C, A>, Pair<C, B>> second() {
    return pOptional(cs -> getOrModify(cs.right()).bimap(t -> Pair.pair(cs.left(), t), a -> Pair.pair(cs.left(), a)),
      cb -> _s -> Pair.pair(cb.left(), set(cb.right()).apply(_s.right())));
  }

  /***************************************************************/
  /** Compose methods between a {@link POptional} and another Optics */
  /***************************************************************/

  /**
   * compose a {@link POptional} with a {@link Fold}
   */
  public final <C> Fold<S, C> composeFold(final Fold<A, C> other) {
    return asFold().composeFold(other);
  }

  /**
   * compose a {@link POptional} with a {@link Getter}
   */
  public final <C> Fold<S, C> composeGetter(final Getter<A, C> other) {
    return asFold().composeGetter(other);
  }

  /**
   * compose a {@link POptional} with a {@link PSetter}
   */
  public final <C, D> PSetter<S, T, C, D> composeSetter(final PSetter<A, B, C, D> other) {
    return asSetter().composeSetter(other);
  }

  /**
   * compose a {@link POptional} with a {@link PTraversal}
   */
  public final <C, D> PTraversal<S, T, C, D> composeTraversal(final PTraversal<A, B, C, D> other) {
    return asTraversal().composeTraversal(other);
  }

  /**
   * compose a {@link POptional} with a {@link POptional}
   */
  public final <C, D> POptional<S, T, C, D> composeOptional(final POptional<A, B, C, D> other) {
    final POptional<S, T, A, B> self = this;
    return new POptional<S, T, C, D>() {

      @Override public Either<T, C> getOrModify(final S s) {
        return self.getOrModify(s).right().flatMap(a -> other.getOrModify(a).bimap(b -> POptional.this.set(b).apply(s), Function.identity()));
      }

      @Override public Function<S, T> set(final D d) {
        return self.modify(other.set(d));
      }

      @Override public Option<C> getOption(final S s) {
        return self.getOption(s).flatMap(other::getOption);
      }

      @Override public <G> Function<S, Function<G, T>> modifyFunctionF(final Function<C, Function<G, D>> f) {
        return self.modifyFunctionF(other.modifyFunctionF(f));
      }

      @Override public <L> Function<S, Either<L, T>> modifyEitherF(final Function<C, Either<L, D>> f) {
        return self.modifyEitherF(other.modifyEitherF(f));
      }

      @Override public Function<S, Option<T>> modifyOptionF(final Function<C, Option<D>> f) {
        return self.modifyOptionF(other.modifyOptionF(f));
      }

      @Override public Function<S, Iterable<T>> modifyIterableF(final Function<C, Iterable<D>> f) {
        return self.modifyIterableF(other.modifyIterableF(f));
      }

      @Override public Function<S, Supplier<T>> modifySupplierF(final Function<C, Supplier<D>> f) {
        return self.modifySupplierF(other.modifySupplierF(f));
      }

      @Override public Function<S, Pair<T, T>> modifyPairF(final Function<C, Pair<D, D>> f) {
        return self.modifyPairF(other.modifyPairF(f));
      }

      @Override public Function<S, T> modify(final Function<C, D> f) {
        return self.modify(other.modify(f));
      }
    };
  }

  /**
   * compose a {@link POptional} with a {@link PPrism}
   */
  public final <C, D> POptional<S, T, C, D> composePrism(final PPrism<A, B, C, D> other) {
    return composeOptional(other.asOptional());
  }

  /**
   * compose a {@link POptional} with a {@link PLens}
   */
  public final <C, D> POptional<S, T, C, D> composeLens(final PLens<A, B, C, D> other) {
    return composeOptional(other.asOptional());
  }

  /**
   * compose a {@link POptional} with a {@link PIso}
   */
  public final <C, D> POptional<S, T, C, D> composeIso(final PIso<A, B, C, D> other) {
    return composeOptional(other.asOptional());
  }

  /*********************************************************************/
  /** Transformation methods to view a {@link POptional} as another Optics */
  /*********************************************************************/

  /**
   * view a {@link POptional} as a {@link Fold}
   */
  public final Fold<S, A> asFold() {
    return new Fold<S, A>() {
      @Override public <M> Function<S, M> foldMap(final Supplier<M> identity, final BinaryOperator<M> op, final Function<A, M> f) {
        return s -> POptional.this.getOption(s).map(f).getOr(identity);
      }
    };
  }

  /**
   * view a {@link POptional} as a {@link PSetter}
   */
  public PSetter<S, T, A, B> asSetter() {
    return new PSetter<S, T, A, B>() {
      @Override public Function<S, T> modify(final Function<A, B> f) {
        return POptional.this.modify(f);
      }

      @Override public Function<S, T> set(final B b) {
        return POptional.this.set(b);
      }
    };
  }

  /**
   * view a {@link POptional} as a {@link PTraversal}
   */
  public PTraversal<S, T, A, B> asTraversal() {
    final POptional<S, T, A, B> self = this;
    return new PTraversal<S, T, A, B>() {

      @Override public <C> Function<S, Function<C, T>> modifyFunctionF(final Function<A, Function<C, B>> f) {
        return self.modifyFunctionF(f);
      }

      @Override public <L> Function<S, Either<L, T>> modifyEitherF(final Function<A, Either<L, B>> f) {
        return self.modifyEitherF(f);
      }

      @Override public Function<S, Option<T>> modifyOptionF(final Function<A, Option<B>> f) {
        return self.modifyOptionF(f);
      }

      @Override public Function<S, Iterable<T>> modifyIterableF(final Function<A, Iterable<B>> f) {
        return self.modifyIterableF(f);
      }

      @Override public Function<S, Supplier<T>> modifySupplierF(final Function<A, Supplier<B>> f) {
        return self.modifySupplierF(f);
      }

      @Override public Function<S, Pair<T, T>> modifyPairF(final Function<A, Pair<B, B>> f) {
        return self.modifyPairF(f);
      }

      @Override public <M> Function<S, M> foldMap(final Supplier<M> identity, final BinaryOperator<M> op, final Function<A, M> f) {
        return s -> self.getOption(s).map(f).getOr(identity);
      }
    };
  }

  public static <S, T> POptional<S, T, S, T> pId() {
    return PIso.<S, T> pId().asOptional();
  }

  /**
   * create a {@link POptional} using the canonical functions: getOrModify and
   * set
   */
  public static <S, T, A, B> POptional<S, T, A, B> pOptional(final Function<S, Either<T, A>> getOrModify, final Function<B, Function<S, T>> set) {
    return new POptional<S, T, A, B>() {
      @Override public Either<T, A> getOrModify(final S s) {
        return getOrModify.apply(s);
      }

      @Override public Function<S, T> set(final B b) {
        return set.apply(b);
      }

      @Override public Option<A> getOption(final S s) {
        return getOrModify.apply(s).right().toOption();
      }

      @Override public <C> Function<S, Function<C, T>> modifyFunctionF(final Function<A, Function<C, B>> f) {
        return s -> getOrModify.apply(s).fold(t -> __ -> t, a -> f.apply(a).andThen(b -> set.apply(b).apply(s)));
      }

      @Override public <L> Function<S, Either<L, T>> modifyEitherF(final Function<A, Either<L, B>> f) {
        return s -> getOrModify.apply(s).fold(Eithers.toRight(), t -> f.apply(t).right().map(b -> set.apply(b).apply(s)));
      }

      @Override public Function<S, Option<T>> modifyOptionF(final Function<A, Option<B>> f) {
        return s -> getOrModify.apply(s).fold(Option::some, t -> f.apply(t).map(b -> set.apply(b).apply(s)));
      }

      @Override public Function<S, Iterable<T>> modifyIterableF(final Function<A, Iterable<B>> f) {
        return s -> getOrModify.apply(s).fold(Collections::singleton, t -> Iterables.<B, T> map(f.apply(t), b -> set.apply(b).apply(s)));
      }

      @Override public Function<S, Supplier<T>> modifySupplierF(final Function<A, Supplier<B>> f) {
        return s -> getOrModify.apply(s).fold(Suppliers::ofInstance, t -> Suppliers.<B, T> compose(b -> set.apply(b).apply(s), f.apply(t)));
      }

      @Override public Function<S, Pair<T, T>> modifyPairF(final Function<A, Pair<B, B>> f) {
        return s -> getOrModify.apply(s).fold(t -> Pair.pair(t, t), t -> Pair.<B, T> map(f.apply(t), b -> set.apply(b).apply(s)));
      }

      @Override public Function<S, T> modify(final Function<A, B> f) {
        return s -> getOrModify.apply(s).fold(Function.identity(), a -> set.apply(f.apply(a)).apply(s));
      }
    };
  }
}
