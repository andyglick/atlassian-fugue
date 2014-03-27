package com.atlassian.fugue;

import static com.atlassian.fugue.Pair.pair;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import com.atlassian.fugue.Iterables.IterableToString;
import com.google.common.base.Function;

public final class Pairs {
  private Pairs() {}

  /**
   * Zips two iterables into a single iterable that produces {@link Pair pairs}.
   * 
   * @param <A> LHS type
   * @param <B> RHS type
   * @param as left values
   * @param bs right values
   * @return an {@link Iterable iterable} of pairs, only as long as the shortest
   * input iterable.
   * @since 1.1
   */
  public static <A, B> Iterable<Pair<A, B>> zip(final Iterable<A> as, final Iterable<B> bs) {
    return Iterables.zip(as, bs);
  }

  /**
   * Unzips an iterable of {@link Pair pairs} into a {@link Pair pair} of
   * iterables.
   * 
   * @param <A> LHS type
   * @param <B> RHS type
   * @param pairs the values
   * @return a {@link Pair pair} of {@link Iterable iterable} of the same length
   * as the input iterable.
   * @since 2.2
   */
  public static <A, B> Pair<Iterable<A>, Iterable<B>> unzip(Iterable<Pair<A, B>> pairs) {
    return pair(View.from(pairs, Pair.<A> leftValue()), View.from(pairs, Pair.<B> rightValue()));
  }

  /**
   * Iterable that combines two iterables using a combiner function.
   */
  static class View<F, A> extends IterableToString<A> {
    static <F, A> Iterable<A> from(Iterable<F> fs, Function<? super F, A> f) {
      return new View<F, A>(fs, f);
    }
    
    private final Iterable<F> fs;
    private final Function<? super F, ? extends A> f;

    View(final Iterable<F> fs, final Function<? super F, ? extends A> f) {
      this.fs = checkNotNull(fs, "fs must not be null.");
      this.f = checkNotNull(f, "f must not be null.");
    }

    @Override public Iterator<A> iterator() {
      return new Iter();
    }

    class Iter implements Iterator<A> {
      private final Iterator<F> it = checkNotNull(fs.iterator(), "as iterator must not be null.");

      @Override public boolean hasNext() {
        return it.hasNext();
      }

      @Override public A next() {
        return f.apply(it.next());
      }

      @Override public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }
}
