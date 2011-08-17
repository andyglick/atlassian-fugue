package com.atlassian.fugue;

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.google.common.collect.Iterators.filter;

import com.google.common.base.Predicate;

import java.util.Iterator;

/**
 * Contains static utility methods that operate on or return objects of type
 * {code}Iterable{code}.
 * 
 * This class is primarily focused around filling holes in Guava's
 * {@link com.google.common.collect.Iterables} class which have become apparent
 * with the addition of Fugue classes such as Option and Either.
 * 
 * When making changes to this class, please try to name methods differently to
 * those in {@link com.google.common.collect.Iterables} so that methods from
 * both classes can be statically imported in the same class.
 * 
 * @since 1.0
 */
public class Iterables {
  private Iterables() {
    throw new UnsupportedOperationException("This class is not instantiable.");
  }

  /**
   * Finds the first item that matches the predicate. Traditionally, this should
   * be named find; in this case it is named findFirst to avoid clashing with
   * static imports from Guava's {@link com.google.common.collect.Iterables}.
   * 
   * @param elements the iterable to search for a matching element
   * @param predicate the predicate to use to determine if an element is
   * eligible to be returned
   * @return the first item in elements that matches predicate
   */
  public static <T> Option<T> findFirst(final Iterable<? extends T> elements, final Predicate<? super T> predicate) {
    final Iterator<? extends T> t = filter(elements.iterator(), predicate);
    if (t.hasNext()) {
      final T next = t.next();
      return some(next);
    }
    return none();
  }
}
