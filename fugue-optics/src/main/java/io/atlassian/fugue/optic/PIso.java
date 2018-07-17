package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Suppliers;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link PIso} defines an isomorphism between types S, A and B, T:
 * 
 * <pre>
 *              get                           reverse.get
 *     --------------------&gt;             --------------------&gt;
 *   S                       A         T                       B
 *     &lt;--------------------             &lt;--------------------
 *       reverse.reverseGet                   reverseGet
 * </pre>
 * <p>
 * In addition, if f and g forms an isomorphism between `A` and `B`, i.e. if `f
 * . g = id` and `g . f = id`, then a {@link PIso} defines an isomorphism
 * between `S` and `T`:
 * 
 * <pre>
 *     S           T                                   S           T
 *     |           ↑                                   ↑           |
 *     |           |                                   |           |
 * get |           | reverseGet     reverse.reverseGet |           | reverse.get
 *     |           |                                   |           |
 *     ↓     f     |                                   |     g     ↓
 *     A --------&gt; B                                   A &lt;-------- B
 * </pre>
 * <p>
 * A {@link PIso} is also a valid {@link Getter}, {@link Fold}, {@link PLens},
 * {@link PPrism}, {@link POptional}, {@link PTraversal} and {@link PSetter}
 *
 * @param <S> the source of a {@link PIso}
 * @param <T> the modified source of a {@link PIso}
 * @param <A> the target of a {@link PIso}
 * @param <B> the modified target of a {@link PIso}
 */
public abstract class PIso<S, T, A, B> {

  PIso() {
    super();
  }

  /**
   * get the target of a {@link PIso}
   */
  public abstract A get(S s);

  /**
   * get the modified source of a {@link PIso}
   */
  public abstract T reverseGet(B b);

  /**
   * reverse a {@link PIso}: the source becomes the target and the target
   * becomes the source
   */
  public abstract PIso<B, A, T, S> reverse();

  /**
   * modify polymorphically the target of a {@link PIso} with an Applicative
   * function
   */
  public final <C> Function<S, Function<C, T>> modifyFunctionF(final Function<A, Function<C, B>> f) {
    return s -> f.apply(get(s)).andThen(this::reverseGet);
  }

  /**
   * modify polymorphically the target of a {@link PIso} with an Applicative
   * function
   */
  public final <L> Function<S, Either<L, T>> modifyEitherF(final Function<A, Either<L, B>> f) {
    return s -> f.apply(get(s)).right().map(this::reverseGet);
  }

  /**
   * modify polymorphically the target of a {@link PIso} with an Applicative
   * function
   */
  public final Function<S, Option<T>> modifyOptionF(final Function<A, Option<B>> f) {
    return s -> f.apply(get(s)).map(this::reverseGet);
  }

  /**
   * modify polymorphically the target of a {@link PIso} with an Applicative
   * function
   */
  public final Function<S, Iterable<T>> modifyIterableF(final Function<A, Iterable<B>> f) {
    return s -> Iterables.map(f.apply(get(s)), this::reverseGet);
  }

  /**
   * modify polymorphically the target of a {@link PIso} with an Applicative
   * function
   */
  public final Function<S, Supplier<T>> modifySupplierF(final Function<A, Supplier<B>> f) {
    return s -> Suppliers.compose(this::reverseGet, f.apply(get(s)));
  }

  /**
   * modify polymorphically the target of a {@link PIso} with an Applicative
   * function
   */
  public final Function<S, Pair<T, T>> modifyPairF(final Function<A, Pair<B, B>> f) {
    return s -> Pair.map(f.apply(get(s)), this::reverseGet);
  }

  /**
   * modify polymorphically the target of a {@link PIso} with a function
   */
  public final Function<S, T> modify(final Function<A, B> f) {
    return s -> reverseGet(f.apply(get(s)));
  }

  /**
   * set polymorphically the target of a {@link PIso} with a value
   */
  public final Function<S, T> set(final B b) {
    return __ -> reverseGet(b);
  }

  /**
   * pair two disjoint {@link PIso}
   */
  public <S1, T1, A1, B1> PIso<Pair<S, S1>, Pair<T, T1>, Pair<A, A1>, Pair<B, B1>> product(final PIso<S1, T1, A1, B1> other) {
    return pIso(ss1 -> Pair.pair(get(ss1.left()), other.get(ss1.right())), bb1 -> Pair.pair(reverseGet(bb1.left()), other.reverseGet(bb1.right())));
  }

  public <C> PIso<Pair<S, C>, Pair<T, C>, Pair<A, C>, Pair<B, C>> first() {
    return pIso(sc -> Pair.pair(get(sc.left()), sc.right()), bc -> Pair.pair(reverseGet(bc.left()), bc.right()));
  }

  public <C> PIso<Pair<C, S>, Pair<C, T>, Pair<C, A>, Pair<C, B>> second() {
    return pIso(cs -> Pair.pair(cs.left(), get(cs.right())), cb -> Pair.pair(cb.left(), reverseGet(cb.right())));
  }

  /**********************************************************/
  /** Compose methods between a {@link PIso} and another Optics */
  /**********************************************************/

  /**
   * compose a {@link PIso} with a {@link Fold}
   */
  public final <C> Fold<S, C> composeFold(final Fold<A, C> other) {
    return asFold().composeFold(other);
  }

  /**
   * compose a {@link PIso} with a {@link Getter}
   */
  public final <C> Getter<S, C> composeGetter(final Getter<A, C> other) {
    return asGetter().composeGetter(other);
  }

  /**
   * compose a {@link PIso} with a {@link PSetter}
   */
  public final <C, D> PSetter<S, T, C, D> composeSetter(final PSetter<A, B, C, D> other) {
    return asSetter().composeSetter(other);
  }

  /**
   * compose a {@link PIso} with a {@link PTraversal}
   */
  public final <C, D> PTraversal<S, T, C, D> composeTraversal(final PTraversal<A, B, C, D> other) {
    return asTraversal().composeTraversal(other);
  }

  /**
   * compose a {@link PIso} with a {@link POptional}
   */
  public final <C, D> POptional<S, T, C, D> composeOptional(final POptional<A, B, C, D> other) {
    return asOptional().composeOptional(other);
  }

  /**
   * compose a {@link PIso} with a {@link PPrism}
   */
  public final <C, D> PPrism<S, T, C, D> composePrism(final PPrism<A, B, C, D> other) {
    return asPrism().composePrism(other);
  }

  /**
   * compose a {@link PIso} with a {@link PLens}
   */
  public final <C, D> PLens<S, T, C, D> composeLens(final PLens<A, B, C, D> other) {
    return asLens().composeLens(other);
  }

  /**
   * compose a {@link PIso} with a {@link PIso}
   */
  public final <C, D> PIso<S, T, C, D> composeIso(final PIso<A, B, C, D> other) {
    final PIso<S, T, A, B> self = this;
    return new PIso<S, T, C, D>() {
      @Override public C get(final S s) {
        return other.get(self.get(s));
      }

      @Override public T reverseGet(final D d) {
        return self.reverseGet(other.reverseGet(d));
      }

      @Override public PIso<D, C, T, S> reverse() {
        final PIso<S, T, C, D> composeSelf = this;
        return new PIso<D, C, T, S>() {
          @Override public T get(final D d) {
            return self.reverseGet(other.reverseGet(d));
          }

          @Override public C reverseGet(final S s) {
            return other.get(self.get(s));
          }

          @Override public PIso<S, T, C, D> reverse() {
            return composeSelf;
          }
        };
      }
    };
  }

  /****************************************************************/
  /** Transformation methods to view a {@link PIso} as another Optics */
  /****************************************************************/

  /**
   * view a {@link PIso} as a {@link Fold}
   */
  public final Fold<S, A> asFold() {
    return new Fold<S, A>() {
      @Override public <M> Function<S, M> foldMap(final Monoid<M> monoid, final Function<A, M> f) {
        return s -> f.apply(PIso.this.get(s));
      }
    };
  }

  /**
   * view a {@link PIso} as a {@link Getter}
   */
  public final Getter<S, A> asGetter() {
    return new Getter<S, A>() {
      @Override public A get(final S s) {
        return PIso.this.get(s);
      }
    };
  }

  /**
   * view a {@link PIso} as a {@link Setter}
   */
  public PSetter<S, T, A, B> asSetter() {
    return new PSetter<S, T, A, B>() {
      @Override public Function<S, T> modify(final Function<A, B> f) {
        return PIso.this.modify(f);
      }

      @Override public Function<S, T> set(final B b) {
        return PIso.this.set(b);
      }
    };
  }

  /**
   * view a {@link PIso} as a {@link PTraversal}
   */
  public PTraversal<S, T, A, B> asTraversal() {
    final PIso<S, T, A, B> self = this;
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

      @Override public <M> Function<S, M> foldMap(final Monoid<M> monoid, final Function<A, M> f) {
        return s -> f.apply(self.get(s));
      }

    };
  }

  /**
   * view a {@link PIso} as a {@link POptional}
   */
  public POptional<S, T, A, B> asOptional() {
    final PIso<S, T, A, B> self = this;
    return new POptional<S, T, A, B>() {
      @Override public Either<T, A> getOrModify(final S s) {
        return Either.right(self.get(s));
      }

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

      @Override public Function<S, T> set(final B b) {
        return self.set(b);
      }

      @Override public Option<A> getOption(final S s) {
        return Option.some(self.get(s));
      }

      @Override public Function<S, T> modify(final Function<A, B> f) {
        return self.modify(f);
      }
    };
  }

  /**
   * view a {@link PIso} as a {@link PPrism}
   */
  public PPrism<S, T, A, B> asPrism() {
    final PIso<S, T, A, B> self = this;
    return new PPrism<S, T, A, B>() {
      @Override public Either<T, A> getOrModify(final S s) {
        return Either.right(self.get(s));
      }

      @Override public T reverseGet(final B b) {
        return self.reverseGet(b);
      }

      @Override public Option<A> getOption(final S s) {
        return Option.some(self.get(s));
      }
    };
  }

  /**
   * view a {@link PIso} as a {@link PLens}
   */
  public PLens<S, T, A, B> asLens() {
    final PIso<S, T, A, B> self = this;
    return new PLens<S, T, A, B>() {
      @Override public A get(final S s) {
        return self.get(s);
      }

      @Override public Function<S, T> set(final B b) {
        return self.set(b);
      }

      @Override public Function<S, T> modify(final Function<A, B> f) {
        return self.modify(f);
      }

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

    };
  }

  /**
   * create a {@link PIso} using a pair of functions: one to get the target and
   * one to get the source.
   */
  public static <S, T, A, B> PIso<S, T, A, B> pIso(final Function<S, A> get, final Function<B, T> reverseGet) {
    return new PIso<S, T, A, B>() {

      @Override public A get(final S s) {
        return get.apply(s);
      }

      @Override public T reverseGet(final B b) {
        return reverseGet.apply(b);
      }

      @Override public PIso<B, A, T, S> reverse() {
        final PIso<S, T, A, B> self = this;
        return new PIso<B, A, T, S>() {
          @Override public T get(final B b) {
            return reverseGet.apply(b);
          }

          @Override public A reverseGet(final S s) {
            return get.apply(s);
          }

          @Override public PIso<S, T, A, B> reverse() {
            return self;
          }
        };
      }

    };
  }

  /**
   * create a {@link PIso} between any type and itself. id is the zero element
   * of optics composition, for all optics o of type O (e.g. Lens, Iso, Prism,
   * ...):
   * 
   * <pre>
   *  o composeIso Iso.id == o
   *  Iso.id composeO o == o
   * </pre>
   * <p>
   * (replace composeO by composeLens, composeIso, composePrism, ...)
   */
  public static <S, T> PIso<S, T, S, T> pId() {
    return new PIso<S, T, S, T>() {

      @Override public S get(final S s) {
        return s;
      }

      @Override public T reverseGet(final T t) {
        return t;
      }

      @Override public PIso<T, S, T, S> reverse() {
        final PIso<S, T, S, T> self = this;
        return new PIso<T, S, T, S>() {
          @Override public T get(final T t) {
            return t;
          }

          @Override public S reverseGet(final S s) {
            return s;
          }

          @Override public PIso<S, T, S, T> reverse() {
            return self;
          }
        };
      }
    };
  }

}
