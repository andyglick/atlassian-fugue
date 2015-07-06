package com.atlassian.fugue;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Class holds implementations necessary to deprecate all functions related to
 * collection code from the base fugue package. All code referenced here has
 * been moved to fugue-collect.
 * @since 3.0
 */
public class DeprecatedCode {

  /**
   * Iterator where {@link #remove} is unsupported.
   * @deprecated since 3.0 Moved to fugue-collect
   */
  @Deprecated
  static abstract class UnmodifiableIterator<E> implements Iterator<E> {
    protected UnmodifiableIterator() {}

    @Deprecated @Override public final void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * @deprecated since 3.0 Moved to fugue-collect
   */
  @Deprecated
  static abstract class AbstractIterator<A> extends UnmodifiableIterator<A> {
    private State state = State.NotReady;

    /** Constructor for use by subclasses. */
    protected AbstractIterator() {}

    private enum State {
      Ready, NotReady, Complete, Failed
    }

    private A next;

    /**
     * The next element.
     * <p>
     * <b>Note:</b> the implementation must call {@link #endOfData()} when there
     * are no elements left in the iteration. Failure to do so could result in an
     * infinite loop.
     */
    protected abstract A computeNext();

    /**
     * Implementations of {@link #computeNext} <b>must</b> invoke this method when
     * there are no elements left in the iteration.
     *
     * @return {@code null}; a convenience so your {@code computeNext}
     * implementation can use the simple statement {@code return endOfData();}
     */
    protected final A endOfData() {
      state = State.Complete;
      return null;
    }

    @Override public final boolean hasNext() {
      switch (state) {
        case Failed:
          throw new IllegalStateException("Failed iterator");
        case Ready:
          return true;
        case Complete:
          return false;
        default:
          return tryToComputeNext();
      }
    }

    private boolean tryToComputeNext() {
      try {
        next = computeNext();
        if (state != State.Complete) {
          state = State.Ready;
          return true;
        }
        return false;
      } catch (RuntimeException | Error e) {
        state = State.Failed;
        throw e;
      }
    }

    @Override public final A next() {
      if (!hasNext())
        throw new NoSuchElementException();
      try {
        return next;
      } finally {
        next = null;
        state = State.NotReady;
      }
    }
  }




  /**
   * Transform and iterable by applying a function to each of it's values
   *
   * @param as the source iterable
   * @param f function to apply to all the elements of as
   * @param <A> original iterable type
   * @param <B> output iterable type
   * @return new iterable containing the transformed values produced by f#apply
   *
   * @since 3.0
   * @deprecated since 3.0 Moved to fugue-collect
   */
  @Deprecated
  static <A, B> Iterable<B> transform(final Iterable<A> as, final Function<? super A, ? extends B> f) {
    return new Transform<>(as, f);
  }

  static final class Transform<A, B> implements Iterable<B> {
    private final Iterable<? extends A> as;
    private final Function<? super A, ? extends B> f;

    Transform(Iterable<? extends A> as, Function<? super A, ? extends B> f) {
      this.as = as;
      this.f = f;
    }

    @Override public Iterator<B> iterator() {
      return new AbstractIterator<B>() {
        private final Iterator<? extends A> it = as.iterator();

        @Override protected B computeNext() {
          if (!it.hasNext()) {
            return endOfData();
          }
          return f.apply(it.next());
        }
      };
    }
  }


  /**
   * Remove elements from the input iterable for which the predicate returns false
   * @param as original iterable
   * @param p predicate to filter by
   * @param <A> element type
   * @return new iterable containing only those elements for which p#test returns true
   *
   * @since 3.0
   * @deprecated since 3.0 Moved to fugue-collect
   */
  @Deprecated
  static <A> Iterable<A> filter(final Iterable<A> as, final Predicate<? super A> p) {
    return new Filter<>(as, p);
  }

  static final class Filter<A> implements Iterable<A> {
    private final Iterable<? extends A> as;
    private final Predicate<? super A> p;

    Filter(Iterable<? extends A> as, Predicate<? super A> p) {
      this.as = as;
      this.p = p;
    }

    @Override public Iterator<A> iterator() {
      return new AbstractIterator<A>() {
        private final Iterator<? extends A> it = as.iterator();

        @Override protected A computeNext() {
          if (!it.hasNext()) {
            return endOfData();
          }
          while (it.hasNext()) {
            A a = it.next();
            if (p.test(a)) {
              return a;
            }
          }
          return endOfData();
        }
      };
    }
  }


  /**
   * Zips two iterables into a single iterable that produces {@link Pair pairs}.
   * See unzip(Iterable) for the opposite operation
   *
   * @param <A> LHS type
   * @param <B> RHS type
   * @param as left values
   * @param bs right values
   * @return an {@link Iterable iterable} of pairs, only as long as the shortest
   * input iterable.
   *
   *
   * @since 1.2
   * @deprecated since 3.0 Moved to fugue-collect
   */
  @Deprecated
  static <A, B> Iterable<Pair<A, B>> zip(final Iterable<A> as, final Iterable<B> bs) {
    return zipWith(Pair.<A, B> pairs()).apply(as, bs);
  }

  /**
   * Takes a two-arg function that returns a third type and reurn a new function
   * that takes iterables of the two input types and combines them into a new
   * iterable.
   *
   * @param <A> LHS type
   * @param <B> RHS type
   * @param <C> result type
   * @param f combiner function
   * @return an Function that takes two iterables and zips them using the
   * supplied function
   * @since 1.2
   * @deprecated since 3.0 Moved to fugue-collect
   */
  @Deprecated
  static <A, B, C> BiFunction<Iterable<A>, Iterable<B>, Iterable<C>> zipWith(final BiFunction<A, B, C> f) {
    return (as, bs) -> new Zipper<>(as, bs, f);
  }


  /**
   * Iterable that combines two iterables using a combiner function.
   */
  static class Zipper<A, B, C> extends IterableToString<C> {
    private final Iterable<A> as;
    private final Iterable<B> bs;
    private final BiFunction<A, B, C> f;

    Zipper(final Iterable<A> as, final Iterable<B> bs, final BiFunction<A, B, C> f) {
      this.as = requireNonNull(as, "as must not be null.");
      this.bs = requireNonNull(bs, "bs must not be null.");
      this.f = requireNonNull(f, "f must not be null.");
    }

    @Override public Iterator<C> iterator() {
      return new Iter();
    }

    class Iter implements Iterator<C> {
      private final Iterator<A> a = requireNonNull(as.iterator(), "as iterator must not be null.");
      private final Iterator<B> b = requireNonNull(bs.iterator(), "bs iterator must not be null.");

      @Override public boolean hasNext() {
        return a.hasNext() && b.hasNext();
      }

      @Override public C next() {
        return f.apply(a.next(), b.next());
      }

      @Override public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }


  static abstract class IterableToString<A> implements Iterable<A> {
    @Override public final String toString() {
      Iterator<A> it = this.iterator();
      StringBuilder buffer = new StringBuilder().append("[");
      while (it.hasNext()) {
        buffer.append(Objects.requireNonNull(it.next()).toString());
        if (it.hasNext()) {
          buffer.append(", ");
        }
      }
      buffer.append("]");
      return buffer.toString();
    }
  }


  /**
   * Filters and maps (aka transforms) the unfiltered iterable.
   *
   * Applies the given partial function to each element of the unfiltered
   * iterable. If the application returns none, the element will be left out;
   * otherwise, the transformed object contained in the Option will be added to
   * the result.
   *
   * @param <A> the input type
   * @param <B> the output type
   * @param from the input iterable
   * @param partial the collecting function
   * @return the collected iterable
   *
   * @deprecated since 3.0 Moved to fugue-collect
   */
  @Deprecated
  static <A, B> Iterable<B> collect(Iterable<? extends A> from, Function<? super A, Option<B>> partial) {
    return new CollectingIterable<>(from, partial);
  }


  /**
   * CollectingIterable, filters and transforms in one.
   */
  static class CollectingIterable<A, B> extends IterableToString<B> {
    private final Iterable<? extends A> delegate;
    private final Function<? super A, Option<B>> partial;

    CollectingIterable(Iterable<? extends A> delegate, Function<? super A, Option<B>> partial) {
      this.delegate = requireNonNull(delegate);
      this.partial = requireNonNull(partial);
    }

    public Iterator<B> iterator() {
      return new Iter();
    }

    final class Iter extends AbstractIterator<B> {
      private final Iterator<? extends A> it = delegate.iterator();

      @Override protected B computeNext() {
        while (it.hasNext()) {
          Option<B> result = partial.apply(it.next());
          if (result.isDefined())
            return result.get();
        }
        return endOfData();
      }
    }
  }


  /**
   * Return the size of an iterable
   * @param as iterable to compute the size of
   * @param <A> element type
   * @return number of elements in the iterable
   *
   * @since 3.0
   * @deprecated since 3.0 Moved to fugue-collect
   */
  @Deprecated
  static <A> int size(Iterable<A> as) {
    if (as instanceof Collection) {
      return ((Collection<?>) as).size();
    } else {
      Iterator<A> iterator = as.iterator();
      int count = 0;
      while (iterator.hasNext()) {
        iterator.next();
        count++;
      }
      return count;
    }
  }


  /**
   * Predicate that checks if an iterable is empty.
   *
   * @return {@code Predicate} which checks if an {@code Iterable} is empty
   * @since 1.1
   * @deprecated since 3.0 Moved to fugue-collect
   */
  @Deprecated
  static Predicate<Iterable<?>> isEmpty() {
    return it -> {
      if (it instanceof Collection) {
        return ((Collection<?>) it).isEmpty();
      }
      return !it.iterator().hasNext();
    };
  }
}
