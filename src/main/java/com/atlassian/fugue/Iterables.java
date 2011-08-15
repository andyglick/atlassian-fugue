package com.atlassian.fugue;

import com.google.common.base.Predicate;

import java.util.Iterator;

import static com.google.common.collect.Iterators.filter;

/**
 * Contains static utility methods that operate on or return objects of type {code}Iterable{code}.
 * 
 * This class is primarily focussed around filling holes in Guava's Iterables class which have become apparent with the
 * addition of Fugue classes such as Option and Either.
 * 
 * When making changes to this class, please try to name methods differently to those in
 * com.google.common.collect.Iterables so that methods from both classes can be statically imported in the same class.
 */
public class Iterables
{
  private Iterables() { throw new UnsupportedOperationException("This class is not instantiable."); }

  /**
   * Finds the first item that matches the predicate. 
   * Traditionally, this should be named find; in this case it ia names findFirst to avoid clashing with static imports
   * from Guava's Iterables.
   * 
   * @param elements the iterable to search for a matching element
   * @param predicate the predicate to use to determine if an element is eligible to be returned
   * @return the first item in elements that matches predicate
   */
  public static <T> Option<T> findFirst(Iterable<? extends T> elements, Predicate<? super T> predicate) {

    Iterator<? extends T> t = filter(elements.iterator(), predicate);
    if (t.hasNext()) {
        return Option.some(t.next());
    }
    return Option.none();
  }
}
