package io.atlassian.fugue.optic;

import io.atlassian.fugue.*;

import java.util.Collections;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * A {@link PTraversal} can be seen as a {@link POptional} generalised to 0 to n
 * targets where n can be infinite.
 * <p>
 * {@link PTraversal} stands for Polymorphic Traversal as it set and modify
 * methods change a type `A` to `B` and `S` to `T`. {@link Traversal} is a
 * {@link PTraversal} restricted to monomorphic updates.
 *
 * @param <S> the source of a {@link PTraversal}
 * @param <T> the modified source of a {@link PTraversal}
 * @param <A> the target of a {@link PTraversal}
 * @param <B> the modified target of a {@link PTraversal}
 */
public abstract class PTraversal<S, T, A, B> {

  /**
   * modify polymorphically the target of a {@link PTraversal} with an
   * Applicative function
   */
  public abstract <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an
   * Applicative function
   */
  public abstract <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an
   * Applicative function
   */
  public abstract Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an
   * Applicative function
   */
  public abstract Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an
   * Applicative function
   */
  public abstract Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an
   * Applicative function
   */
  public abstract Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> f);

  /**
   * map each target to a {@link Monoid} and combine the results
   */
  public abstract <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f);

  /**
   * combine all targets using a target's {@link Monoid}
   */
  public final Function<S, A> fold(final Monoid<A> monoid) {
    return foldMap(monoid, Function.identity());
  }

  /**
   * get all the targets of a {@link PTraversal}
   */
  public final Iterable<A> getAll(final S s) {
    return foldMap(Monoids.iterable(), Collections::singleton).apply(s);
  }

  /**
   * find the first target of a {@link PTraversal} matching the predicate
   */
  public final Function<S, Option<A>> find(final Predicate<A> p) {
    return foldMap(Monoids.firstOption(), a -> p.test(a) ? Option.some(a) : Option.none());
  }

  /**
   * get the first target of a {@link PTraversal}
   */
  public final Option<A> headOption(final S s) {
    return find(__ -> (Boolean.TRUE)).apply(s);
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
  public final Predicate<S> all(final Predicate<A> p) {
    return foldMap(Monoids.conjunction, p::test)::apply;
  }

  /**
   * modify polymorphically the target of a {@link PTraversal} with a function
   */
  public final Function<S, T> modify(final Function<A, B> f) {
    return s -> this.modifySupplierF(a -> Suppliers.ofInstance(f.apply(a))).apply(s).get();
  }

  /**
   * set polymorphically the target of a {@link PTraversal} with a value
   */
  public final Function<S, T> set(final B b) {
    return modify(__ -> (b));
  }

  /**
   * join two {@link PTraversal} with the same target
   */
  public final <S1, T1> PTraversal<Either<S, S1>, Either<T, T1>, A, B> sum(final PTraversal<S1, T1, A, B> other) {
    final PTraversal<S, T, A, B> self = this;
    return new PTraversal<Either<S, S1>, Either<T, T1>, A, B>() {

      @Override public <C> Function<Either<S, S1>, Function<C, Either<T, T1>>> modifyFunctionF(final Function<A, Function<C, B>> f) {
        return ss1 -> ss1.fold(s -> self.modifyFunctionF(f).apply(s).andThen(Eithers.toLeft()),
          s1 -> other.modifyFunctionF(f).apply(s1).andThen(Eithers.toRight()));
      }

      @Override public <L> Function<Either<S, S1>, Either<L, Either<T, T1>>> modifyEitherF(final Function<A, Either<L, B>> f) {
        return ss1 -> ss1.fold(s -> self.modifyEitherF(f).apply(s).right().map(Eithers.toLeft()),
          s1 -> other.modifyEitherF(f).apply(s1).right().map(Eithers.toRight()));
      }

      @Override public Function<Either<S, S1>, Option<Either<T, T1>>> modifyOptionF(final Function<A, Option<B>> f) {
        return ss1 -> ss1.fold(s -> self.modifyOptionF(f).apply(s).map(Eithers.toLeft()),
          s1 -> other.modifyOptionF(f).apply(s1).map(Eithers.toRight()));
      }

      @Override public Function<Either<S, S1>, Iterable<Either<T, T1>>> modifyIterableF(final Function<A, Iterable<B>> f) {
        return ss1 -> ss1.fold(s -> Iterables.map(self.modifyIterableF(f).apply(s), Eithers.toLeft()),
          s1 -> Iterables.map(other.modifyIterableF(f).apply(s1), Eithers.toRight()));
      }

      @Override public Function<Either<S, S1>, Supplier<Either<T, T1>>> modifySupplierF(final Function<A, Supplier<B>> f) {
        return ss1 -> ss1.fold(s -> Suppliers.compose(Eithers.toLeft(), self.modifySupplierF(f).apply(s)),
          s1 -> Suppliers.compose(Eithers.toRight(), other.modifySupplierF(f).apply(s1)));
      }

      @Override public Function<Either<S, S1>, Pair<Either<T, T1>, Either<T, T1>>> modifyPairF(final Function<A, Pair<B, B>> f) {
        return ss1 -> ss1.fold(s -> Pair.map(self.modifyPairF(f).apply(s), Eithers.toLeft()),
          s1 -> Pair.map(other.modifyPairF(f).apply(s1), Eithers.toRight()));
      }

      @Override public <M> Function<Either<S, S1>, M> foldMap(final Monoid<M> monoid, final Function<A, M> f) {
        return ss1 -> ss1.fold(self.foldMap(monoid, f), other.foldMap(monoid, f));
      }

    };
  }

  /****************************************************************/
  /** Compose methods between a {@link PTraversal} and another Optics */
  /****************************************************************/

  /**
   * compose a {@link PTraversal} with a {@link Fold}
   */
  public final <C> Fold<S, C> composeFold(final Fold<A, C> other) {
    return asFold().composeFold(other);
  }

  //

  /**
   * compose a {@link PTraversal} with a {@link Getter}
   */
  public final <C> Fold<S, C> composeFold(final Getter<A, C> other) {
    return asFold().composeGetter(other);
  }

  /**
   * compose a {@link PTraversal} with a {@link PSetter}
   */
  public final <C, D> PSetter<S, T, C, D> composeSetter(final PSetter<A, B, C, D> other) {
    return asSetter().composeSetter(other);
  }

  /**
   * compose a {@link PTraversal} with a {@link PTraversal}
   */
  public final <C, D> PTraversal<S, T, C, D> composeTraversal(final PTraversal<A, B, C, D> other) {
    final PTraversal<S, T, A, B> self = this;
    return new PTraversal<S, T, C, D>() {

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

      @Override public <M> Function<S, M> foldMap(final Monoid<M> monoid, final Function<C, M> f) {
        return self.foldMap(monoid, other.foldMap(monoid, f));
      }
    };
  }

  /**
   * compose a {@link PTraversal} with a {@link POptional}
   */
  public final <C, D> PTraversal<S, T, C, D> composeOptional(final POptional<A, B, C, D> other) {
    return composeTraversal(other.asTraversal());
  }

  /**
   * compose a {@link PTraversal} with a {@link PPrism}
   */
  public final <C, D> PTraversal<S, T, C, D> composePrism(final PPrism<A, B, C, D> other) {
    return composeTraversal(other.asTraversal());
  }

  /**
   * compose a {@link PTraversal} with a {@link PLens}
   */
  public final <C, D> PTraversal<S, T, C, D> composeLens(final PLens<A, B, C, D> other) {
    return composeTraversal(other.asTraversal());
  }

  /**
   * compose a {@link PTraversal} with a {@link PIso}
   */
  public final <C, D> PTraversal<S, T, C, D> composeIso(final PIso<A, B, C, D> other) {
    return composeTraversal(other.asTraversal());
  }

  /**********************************************************************/
  /** Transformation methods to view a {@link PTraversal} as another Optics */
  /**********************************************************************/

  /**
   * view a {@link PTraversal} as a {@link Fold}
   */
  public final Fold<S, A> asFold() {
    return new Fold<S, A>() {
      @Override public <M> Function<S, M> foldMap(final Monoid<M> monoid, final Function<A, M> f) {
        return PTraversal.this.foldMap(monoid, f);
      }
    };
  }

  /**
   * view a {@link PTraversal} as a {@link PSetter}
   */
  public PSetter<S, T, A, B> asSetter() {
    return PSetter.pSetter(this::modify);
  }

  public static <S, T> PTraversal<S, T, S, T> pId() {
    return PIso.<S, T> pId().asTraversal();
  }

  public static <S, T> PTraversal<Either<S, S>, Either<T, T>, S, T> pCodiagonal() {
    return new PTraversal<Either<S, S>, Either<T, T>, S, T>() {

      @Override public <C> Function<Either<S, S>, Function<C, Either<T, T>>> modifyFunctionF(final Function<S, Function<C, T>> f) {
        return s -> s.bimap(f, f).fold(f1 -> f1.andThen(Eithers.toLeft()), f1 -> f1.andThen(Eithers.toRight()));
      }

      @Override public <L> Function<Either<S, S>, Either<L, Either<T, T>>> modifyEitherF(final Function<S, Either<L, T>> f) {
        return s -> s.bimap(f, f).fold(e -> e.right().map(Eithers.toLeft()), e -> e.right().map(Eithers.toRight()));
      }

      @Override public Function<Either<S, S>, Option<Either<T, T>>> modifyOptionF(final Function<S, Option<T>> f) {
        return s -> s.bimap(f, f).fold(o -> o.map(Eithers.toLeft()), o -> o.map(Eithers.toRight()));
      }

      @Override public Function<Either<S, S>, Iterable<Either<T, T>>> modifyIterableF(final Function<S, Iterable<T>> f) {
        return s -> s.bimap(f, f).fold(ts -> Iterables.map(ts, Eithers.toLeft()), ts -> Iterables.map(ts, Eithers.toRight()));
      }

      @Override public Function<Either<S, S>, Supplier<Either<T, T>>> modifySupplierF(final Function<S, Supplier<T>> f) {
        return s -> s.bimap(f, f).fold(p1 -> Suppliers.compose(Eithers.toLeft(), p1), p1 -> Suppliers.compose(Eithers.toRight(), p1));
      }

      @Override public Function<Either<S, S>, Pair<Either<T, T>, Either<T, T>>> modifyPairF(final Function<S, Pair<T, T>> f) {
        return s -> s.bimap(f, f).fold(tt -> Pair.map(tt, Eithers.toLeft()), tt -> Pair.map(tt, Eithers.toRight()));
      }

      @Override public <M> Function<Either<S, S>, M> foldMap(final Monoid<M> monoid, final Function<S, M> f) {
        return s -> s.fold(f, f);
      }
    };
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final Function<S, A> get1, final Function<S, A> get2,
    final BiFunction<B, B, Function<S, T>> set) {
    return new PTraversal<S, T, A, B>() {

      @Override public <C> Function<S, Function<C, T>> modifyFunctionF(final Function<A, Function<C, B>> f) {
        return s -> Functions.ap(f.apply(get2.apply(s)), f.apply(get1.apply(s)).andThen(b1 -> b2 -> set.apply(b1, b2).apply(s)));
      }

      @Override public <L> Function<S, Either<L, T>> modifyEitherF(final Function<A, Either<L, B>> f) {
        return s -> f.apply(get2.apply(s)).right().ap(f.apply(get1.apply(s)).right().<Function<B, T>> map(b1 -> b2 -> set.apply(b1, b2).apply(s)));
      }

      @Override public Function<S, Option<T>> modifyOptionF(final Function<A, Option<B>> f) {
        return s -> Options.ap(f.apply(get2.apply(s)), f.apply(get1.apply(s)).<Function<B, T>> map(b1 -> b2 -> set.apply(b1, b2).apply(s)));
      }

      @Override public Function<S, Iterable<T>> modifyIterableF(final Function<A, Iterable<B>> f) {
        return s -> Iterables.ap(f.apply(get2.apply(s)), Iterables.map(f.apply(get1.apply(s)), b1 -> b2 -> set.apply(b1, b2).apply(s)));
      }

      @Override public Function<S, Supplier<T>> modifySupplierF(final Function<A, Supplier<B>> f) {
        return s -> Suppliers.ap(f.apply(get2.apply(s)), Suppliers.compose(b1 -> b2 -> set.apply(b1, b2).apply(s), f.apply(get1.apply(s))));
      }

      @Override public Function<S, Pair<T, T>> modifyPairF(final Function<A, Pair<B, B>> f) {
        return s -> Pair.ap(f.apply(get2.apply(s)), Pair.map(f.apply(get1.apply(s)), b1 -> b2 -> set.apply(b1, b2).apply(s)));
      }

      @Override public <M> Function<S, M> foldMap(final Monoid<M> monoid, final Function<A, M> f) {
        return s -> monoid.append(f.apply(get1.apply(s)), f.apply(get2.apply(s)));
      }
    };
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final Function<S, A> get1, final Function<S, A> get2, final Function<S, A> get3,
    final Function<B, Function<B, Function<B, Function<S, T>>>> set) {
    return fromCurried(pTraversal(get1, get2, (b1, b2) -> s -> (b3 -> set.apply(b1).apply(b2).apply(b3).apply(s))), get3);
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final Function<S, A> get1, final Function<S, A> get2, final Function<S, A> get3,
    final Function<S, A> get4, final Function<B, Function<B, Function<B, Function<B, Function<S, T>>>>> set) {
    return fromCurried(pTraversal(get1, get2, get3, b1 -> b2 -> b3 -> s -> b4 -> set.apply(b1).apply(b2).apply(b3).apply(b4).apply(s)), get4);
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final Function<S, A> get1, final Function<S, A> get2, final Function<S, A> get3,
    final Function<S, A> get4, final Function<S, A> get5, final Function<B, Function<B, Function<B, Function<B, Function<B, Function<S, T>>>>>> set) {
    return fromCurried(
      pTraversal(get1, get2, get3, get4, b1 -> b2 -> b3 -> b4 -> s -> b5 -> set.apply(b1).apply(b2).apply(b3).apply(b4).apply(b5).apply(s)), get5);
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final Function<S, A> get1, final Function<S, A> get2, final Function<S, A> get3,
    final Function<S, A> get4, final Function<S, A> get5, final Function<S, A> get6,
    final Function<B, Function<B, Function<B, Function<B, Function<B, Function<B, Function<S, T>>>>>>> set) {
    return fromCurried(
      pTraversal(get1, get2, get3, get4, get5,
        b1 -> b2 -> b3 -> b4 -> b5 -> s -> b6 -> set.apply(b1).apply(b2).apply(b3).apply(b4).apply(b5).apply(b6).apply(s)), get6);
  }

  private static <S, T, A, B> PTraversal<S, T, A, B> fromCurried(final PTraversal<S, Function<B, T>, A, B> curriedTraversal,
    final Function<S, A> lastGet) {
    return new PTraversal<S, T, A, B>() {

      @Override public <C> Function<S, Function<C, T>> modifyFunctionF(final Function<A, Function<C, B>> f) {
        return s -> Functions.ap(f.apply(lastGet.apply(s)), curriedTraversal.modifyFunctionF(f).apply(s));
      }

      @Override public <L> Function<S, Either<L, T>> modifyEitherF(final Function<A, Either<L, B>> f) {
        return s -> f.apply(lastGet.apply(s)).right().ap(curriedTraversal.modifyEitherF(f).apply(s));
      }

      @Override public Function<S, Option<T>> modifyOptionF(final Function<A, Option<B>> f) {
        return s -> Options.ap(f.apply(lastGet.apply(s)), curriedTraversal.modifyOptionF(f).apply(s));
      }

      @Override public Function<S, Iterable<T>> modifyIterableF(final Function<A, Iterable<B>> f) {
        return s -> Iterables.ap(f.apply(lastGet.apply(s)), curriedTraversal.modifyIterableF(f).apply(s));
      }

      @Override public Function<S, Supplier<T>> modifySupplierF(final Function<A, Supplier<B>> f) {
        return s -> Suppliers.ap(f.apply(lastGet.apply(s)), curriedTraversal.modifySupplierF(f).apply(s));
      }

      @Override public Function<S, Pair<T, T>> modifyPairF(final Function<A, Pair<B, B>> f) {
        return s -> Pair.ap(f.apply(lastGet.apply(s)), curriedTraversal.modifyPairF(f).apply(s));
      }

      @Override public <M> Function<S, M> foldMap(final Monoid<M> monoid, final Function<A, M> f) {
        return s -> monoid.append(curriedTraversal.foldMap(monoid, f).apply(s), f.apply(lastGet.apply(s)));
      }
    };
  }
}
